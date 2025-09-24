package system;

import Collections.Vehicle;
import Generators.VehicleAsker;
import client.Client;
import exceptions.NoArgumentException;
import exceptions.WrongArgumentException;
import managers.ExecuteScriptCommand;
import managers.HistoryCommand;
import protocol.DatagramPart;
import protocol.MessageAssembler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private static InetSocketAddress address;
    private static DatagramChannel channel;
    private static Deque<String> lastTwelveCommands = new LinkedList<>();

    public void initialize(String host, int port) throws IOException {
        address = new InetSocketAddress(host,port);
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        System.out.println("Hello! Program waiting your command...");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter command: ");
        try {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();
                try {
                    if (command.equals("exit")) {
                        System.exit(1);
                    }
                    if (!Client.isAuth()) {
                    if (command.equals("register") || command.equals("login")) {
                        AuthCommandsHandler(command, scanner);
                    }else {
                        System.out.println("Unauthorized access. Please login or register to proceed.");
                    }
                    } else {
                        try {
                            if (command.equals("register") || command.equals("login")) {
                                System.out.println("You are already logged in");
                                continue;
                            }
                                otherCommandHandler(command, scanner);
                            } catch (IllegalStateException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                } catch (IOException e) {
                    System.out.println("Server is not availdable");
                    System.out.println(e.getMessage() + " " + e);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (NullPointerException e) {
            System.out.println("");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private void AuthCommandsHandler(String command, Scanner scanner) throws IOException {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            char[] password = scanner.nextLine().trim().toCharArray();
            Client client = Client.getInstance(username, password);
            Request request = new Request(command, null, null, username, password);
            sendRequest(request);
        }catch (IOException e) {
            System.out.println("Something wrong");
        }
    }

    private void otherCommandHandler(String command, Scanner scanner) throws IOException {
        try {
            if (!command.isEmpty()) {
                synchronized (lastTwelveCommands) {
                    if (lastTwelveCommands.size() >= 12) {
                        lastTwelveCommands.removeLast();
                    }
                    lastTwelveCommands.addFirst(command);
                }
                Vehicle vehicle = new Vehicle();
                String key = null;
                boolean isClientCommand = false;
                if (command.contains("removeLower") || command.contains("removeById") || command.contains("countByFuelType") || command.contains("countLessThenFuelType")) {
                    if (command.split(" ").length == 2) {
                        key = command.split(" ")[1];
                    }
                } else if (command.equals("add") || command.equals("addIfMax")) {
                    vehicle = VehicleAsker.createVehicle();
                } else if (command.split(" ")[0].equals("executeScript")) {
                    ExecuteScriptCommand.execute(command);
                    isClientCommand = true;
                } else if (command.split(" ")[0].equals("updateId")) {
                    if (command.split(" ").length == 2) {
                        key = command.split(" ")[1];
                    } else {
                        throw new NoArgumentException("id");
                    }
                    vehicle = VehicleAsker.createVehicle();
                    vehicle.setId(Long.parseLong(key));
                } else if (command.split(" ")[0].equals("history")) {
                    if (command.split(" ").length == 1) {
                        HistoryCommand.execute(lastTwelveCommands);
                        isClientCommand = true;
                    } else {
                        throw new WrongArgumentException(command.split(" ")[1]);
                    }
                }
                if (!isClientCommand) {
                    Request request = new Request(command, vehicle, key, Client.getInstance().getName(), Client.getInstance().getPasswd());
                    sendRequest(request);
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
            Request Request_server = getAnswer();
            if (Request_server.getMessage().contains("Authentication successful")) {
                Client.getInstance(Request_server.getName(), Request_server.getPasswd()).setAuth(true);
            }
            System.out.println("Server answer: \n" + Request_server.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Wrong answer from server");
        } catch (IOException e) {
            System.out.println("Something wrong while reading answer from server");
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
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

    public static Deque<String> getLastTwelveCommands() {
        return lastTwelveCommands;
    }
}
