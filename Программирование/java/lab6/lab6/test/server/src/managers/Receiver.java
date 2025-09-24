package managers;

import Generators.IdGenerator;
import exceptions.NoElementException;
import exceptions.RootException;
import exceptions.WrongArgumentException;
import managers.Command.BaseCommand;
import system.Request;
import system.Server;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;

public class Receiver {
    public static String addNewEl(Request request) throws WrongArgumentException {
        request.getVehicle().setId(IdGenerator.generateId());
        request.getVehicle().setCreationDate(new Date());
        if (request.getMessage().split(" ").length == 1 ) {
            CollectionManager.addToCollection(request.getVehicle());
            return "Element was added";
        }else throw new WrongArgumentException("Add command must not contain arguments");
    }

    public static String addIfMax(Request request) throws WrongArgumentException {
        request.getVehicle().setId(IdGenerator.generateId());
        request.getVehicle().setCreationDate(new Date());
        if(request.getMessage().split(" ").length == 1) {
            return CollectionManager.addIfMax(request);
        } else {
            throw new WrongArgumentException("AddIfMax command must not contain arguments");
        }
    }

    public static String updateId(Request request) throws WrongArgumentException {
        if(request.getMessage().split(" ").length == 2) {
           return CollectionManager.updateId(request);
        } else {
            throw new WrongArgumentException("updateId command must contain only one required argument");
        }
    }

    public static String clearCollection(Request request) throws WrongArgumentException {
        if(request.getMessage().split(" ").length == 1) {
            if(!CollectionManager.getVehicleCollection().isEmpty()) {
                CollectionManager.clearCollection();
                return "Collection cleared";
            } else {
                return "Collection already cleared";
            }
        } else {
            throw new WrongArgumentException("Clear command must not contain arguments");
        }
    }

    public static String exit(Request request) throws WrongArgumentException {
        if(request.getMessage().split(" ").length == 1) {
            System.exit(1);
            return "";
        } else {
            throw new WrongArgumentException("Exit command must not contain arguments");
        }
    }

    public static String removeById(Request request) throws WrongArgumentException, NoElementException{
        if(request.getMessage().split(" ").length == 2) {
            return CollectionManager.removeById(request);
        } else {
            throw new WrongArgumentException("removeById command must contain only one required argument");
        }
    }

    public static String getInfo(Request request) throws WrongArgumentException {
        StringBuilder text = new StringBuilder("");
        if (request.getMessage().split(" ").length == 1) {
            text.append("Data type: " + CollectionManager.getVehicleCollection().getClass());
            text.append("\nInit data: " + CollectionManager.getInstance().getCreationDate());
            text.append("\nSize: " + CollectionManager.getVehicleCollection().size());
            return text.toString();
        } else {
            throw new WrongArgumentException("Info command must not contain arguments");
        }
    }

    public static String getHistory(Request request) throws WrongArgumentException {
        StringBuilder text = new StringBuilder();
        if (request.getMessage().split(" ").length == 1) {
            String[] sp = new String[12];
            int n = 0;
            for (BaseCommand command : CommandManager.lastTwelveCommand) {
                sp[n] = command.getName();
                n += 1;
            }
            for (int i = sp.length - 1; i >= 0; i--) {
                if (sp[i] != null) {
                    text.append("-" + sp[i]).append("\n");
                }
            }
            if(CommandManager.lastTwelveCommand.isEmpty()) {
                System.out.println("history is empty");
            }
            return text.toString();
        } else {
            throw new WrongArgumentException("History command must not contain arguments");
        }
    }

    public static String getHelp(Request request) throws WrongArgumentException {
        StringBuilder text = new StringBuilder("");
        if (request.getMessage().split(" ").length == 1) {
            LinkedHashMap<String, BaseCommand> commandList = CommandManager.getCommandList();
            int maxNameLenght = 0;
            for (String name : commandList.keySet()) {
                if (name.length() > maxNameLenght) {
                    maxNameLenght = name.length();
                }
            }
            String formatString = "%-" + (maxNameLenght + 2) + "s - %s\n";
            for (String name : commandList.keySet()) {
                if(!name.equals("save")) {
                    BaseCommand command = commandList.get(name);
                    text.append(String.format(formatString, command.getName(), command.getDescription()));
                }
            }
            String executeScriptDescription = "Execute script from file.";
            text.append(String.format(formatString, "executeScript {file}", executeScriptDescription));
            return text.toString();
        } else {
            throw new WrongArgumentException("Help command must not contain arguments");
        }
    }

    public static String saveData(Request request) throws IOException, RootException, WrongArgumentException {
        if (request.getMessage().split(" ").length == 1) {
            try {
                FileManager.getInstance(Server.data_path).writeCollection(CollectionManager.getVehicleCollection());
            }catch (Exception e) {
                System.out.println(e.getMessage());
                throw e;
            }
        } else {
            throw new WrongArgumentException("Save command must not contain arguments");
        }
        return "Data was saved";
    }

    public static String showData(Request request) throws WrongArgumentException {
        if (request.getMessage().split(" ").length == 1) {
            return CollectionManager.getInstance().showCollection();
        } else {
            throw new WrongArgumentException("show command must not contain arguments");
        }
    }

    public static String removeLower(Request request) throws WrongArgumentException {
        if (request.getMessage().split(" ").length == 2) {
            return CollectionManager.removeLower(request);
        } else {
            throw new WrongArgumentException("removeLower command must contain only one required argument");
        }
    }

    public static String groupCountingByCreationDate(Request request) throws WrongArgumentException {
        if (request.getMessage().split(" ").length == 1) {
            return CollectionManager.GroupCountingByCreationDate(request);
        } else {
            throw new WrongArgumentException("groupCountingByCreationDate command must not contain arguments");
        }
    }

    public static String countByFuelType(Request request) throws WrongArgumentException {
        if (request.getMessage().split(" ").length == 2) {
            return CollectionManager.countByFuelType(request);
        } else {
            throw new WrongArgumentException("countByFuelType command must contain only one required argument");
        }
    }

    public static String countLessThenFuelType(Request request) throws WrongArgumentException {
        if (request.getMessage().split(" ").length == 2) {
            return CollectionManager.countLessThenFuelType(request);
        } else {
            throw new WrongArgumentException("countLessThenFuelType command must contain only one required argument");
        }
    }







}
