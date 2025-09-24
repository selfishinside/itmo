package system;

import Collections.Vehicle;
import Generators.VehicleAsker;
import exceptions.NoArgumentException;
import exceptions.WrongArgumentException;
import managers.ExecuteScriptCommand;
import protocol.DatagramPart;
import protocol.MessageAssembler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private static InetSocketAddress address;
    private static DatagramChannel channel;

    public void initialize(String host, int port) throws IOException {
        address = new InetSocketAddress(host,port);
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        System.out.println("Hello! Program waiting your command...");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        try {
        while(scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.equals("exit")) {
                System.exit(1);
            }
            if (!command.isEmpty()) {

                Vehicle vehicle = new Vehicle();
                String key = null;
                boolean isExecuteScriptCommand = false;
                if (command.contains("removeLower") || command.contains("removeById") || command.contains("countByFuelType") || command.contains("countLessThenFuelType")) {
                    if (command.split(" ").length == 2) {
                        key = command.split(" ")[1];
                    }
                }else if (command.equals("add") || command.equals("addIfMax")) {
                    vehicle = VehicleAsker.createVehicle();
                }else if (command.split(" ")[0].equals("executeScript")) {
                    ExecuteScriptCommand.execute(command);
                    isExecuteScriptCommand = true;
                }else if (command.split(" ")[0].equals("updateId")) {
                    if (command.split(" ").length == 2) {
                        key = command.split(" ")[1];
                    }
                    vehicle = VehicleAsker.createVehicle();
                }
                if (!isExecuteScriptCommand) {
                    Request request = new Request(command, vehicle, key);
                    sendRequest(request);
                }
            }
        }
        }catch (IOException e) {
            System.out.println("Server is not availdable");
            System.out.println(e.getMessage() + " " + e);
        }catch (WrongArgumentException | NoArgumentException e) {
            System.out.println(e.getMessage());
        }catch (NullPointerException e) {
            System.out.println("");
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static  void sendRequest(Request request) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(request);
        objectOutputStream.flush();
        byte[] data = byteArrayOutputStream.toByteArray();

        List<ByteBuffer> parts = DatagramPart.splitDataIntoParts(data);

        for (ByteBuffer part : parts) {
            channel.send(part, address);
        }
        try {
            Request request_server = getAnswer();
            System.out.println("Server answer: \n" + request_server.getMessage());
        }catch (ClassNotFoundException e) {
            System.out.println("Wrong answer from server");
        }catch (IOException e) {
            System.out.println("Something wrong while reading answer from server");
            System.out.println(e.getMessage());
        }catch (InterruptedException e) {
            System.out.println("Something wrong");
        }
    }

    public static Request getAnswer() throws IOException, InterruptedException, ClassNotFoundException {
        MessageAssembler assembler = new MessageAssembler();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        long timeout = 0;
        try {
            while (true) {
                buffer.clear();
                SocketAddress serverAddres = channel.receive(buffer);
                if (serverAddres != null) {
                    buffer.flip();
                    DatagramPart part = DatagramPart.deserialize(buffer);
                    if(assembler.addPart(part)) {
                        break;
                    }
                }
                Thread.sleep(1000);
                timeout += 1000;
                if(timeout > 10000) {
                    System.out.println("Timeout: Server did not respond in 10 seconds");
                    return null;
                }
            }
            timeout = 0;

            byte[] completeData = assembler.assembleMessage();

            ByteArrayInputStream bi = new ByteArrayInputStream(completeData);
            ObjectInputStream oi = new ObjectInputStream(bi);
            try {
                return (Request) oi.readObject();
            } finally {
                oi.close();
            }
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
