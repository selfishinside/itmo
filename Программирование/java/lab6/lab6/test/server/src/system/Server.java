package system;

import Collections.Vehicle;
import exceptions.RootException;
import managers.CollectionManager;
import managers.CommandManager;
import managers.FileManager;
import managers.Receiver;
import protocol.DatagramPart;
import protocol.MessageAssembler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Server {
    private InetSocketAddress address;
    private DatagramChannel channel;
    private File file;
    public static String data_path = null;
    private Selector selector;

    public void initialize(int port, String filePath) throws IOException, RootException {
        this.address = new InetSocketAddress(port);
        Logger.getLogger(Server.class.getName()).info("Server was started at address: " + address);
        this.channel = DatagramChannel.open();
        this.channel.bind(address);
        this.channel.configureBlocking(false);
        this.selector = Selector.open();
        this.file = new File(filePath);

        if (file.canRead() && file.canWrite()) {
            new CommandManager();
            try {
                Logger.getLogger(Server.class.getName()).info("Downloading data from file...");
                CollectionManager.getInstance().setVehicleCollection(FileManager.getInstance(filePath).readCollection());
                data_path = filePath;
                Logger.getLogger(Server.class.getName()).info("Data was downloaded");
            } catch (Exception e) {
                Logger.getLogger(Server.class.getName()).warning("Error while reading file\n" + filePath);
                System.out.println(e.getMessage());
                System.exit(0);
            }
        } else {
            Logger.getLogger(Server.class.getName()).warning("You do not have enough root to write or read file");
            throw new RootException();
        }
        Logger.getLogger(Server.class.getName()).info("Server is initialized");
    }

    public void start() {
        Logger.getLogger(Server.class.getName()).info("Server is available");
        try {
            channel.register(selector, SelectionKey.OP_READ);
            ByteBuffer readBuffer = ByteBuffer.allocate(8192);
            MessageAssembler messageAssembler = new MessageAssembler();
            new Thread(() -> {
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    try {
                        String input = consoleReader.readLine();
                        if (input.equals("exit") || input.equals("save")) {
                            CommandManager.startExecutingServerMode(new Request(input, null, null));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.out.println("Something went wrong");
                    }
                }
            }).start();


            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        readBuffer.clear();
                        SocketAddress clienAddress = channel.receive(readBuffer);
                        if (clienAddress != null) {
                            readBuffer.flip();
                            Request request = readRequest(readBuffer, clienAddress);
                            if (request != null) {
                                String responseMessage = CommandManager.startExecutingClientMode(request);
                                Request response = new Request(responseMessage, new Vehicle(), null);
                                sendAnswer(channel, response, clienAddress);
                            }
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.exit(1);
        } catch (Exception e) {
            Logger.getLogger(Server.class.getName()).warning("Something wrong with server\n" + e.getMessage());
            System.exit(0);
        }
    }

    private Request readRequest(ByteBuffer readBuffer, SocketAddress clienAddress) throws IOException, ClassNotFoundException {
        MessageAssembler messageAssembler = new MessageAssembler();

            DatagramPart part = DatagramPart.deserialize(readBuffer);
            boolean isComplete = messageAssembler.addPart(part);

            if(isComplete) {
                byte[] completeMessageData = messageAssembler.assembleMessage();

                ByteArrayInputStream bi = new ByteArrayInputStream(completeMessageData);
                ObjectInputStream oi = new ObjectInputStream(bi);
                try {
                    Logger.getLogger(Server.class.getName()).info("Request from client");
                    return (Request) oi.readObject();
                }catch (IOException | ClassNotFoundException e) {
                    Logger.getLogger(Server.class.getName()).warning("Something wrong with request from");
                }
                finally {
                    oi.close();
                }
        }
        return null;
    }

    public void sendAnswer(DatagramChannel channel, Request request, SocketAddress clientAddress) throws IOException {
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();

            List<ByteBuffer> parts = DatagramPart.splitDataIntoParts(bytes);

            for(ByteBuffer part : parts) {
                channel.send(part, clientAddress);
                Logger.getLogger(Server.class.getName()).info("Answer was sent to client");
            }
        } catch (IOException e) {
            throw e;
        } catch (BufferUnderflowException e) {
            System.out.println();
        }
    }
}








