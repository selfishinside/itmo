package managers;
import Collections.Vehicle;
import Generators.IdGenerator;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import comparators.VehicleEnginePowerComparator;
import exceptions.NoArgumentException;
import exceptions.NoElementException;
import system.Request;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс отвечает за взаимодействие с коллекцией на базовом уровне
 *
 * @see Vehicle
 * @author zxc
 * @since 1.0
 */

public class CollectionManager {
    @XStreamImplicit
    private static HashSet<Vehicle> vehicleCollection;
    private final java.util.Date creationDate;
    private static CollectionManager instance;

    /**
     * Базовый конструктор
     *
     * @since 1.0
     */
    private CollectionManager() {
        vehicleCollection = new HashSet<>();
        creationDate = new Date();
    }

    public static synchronized CollectionManager getInstance() {
        if (instance == null) {
            instance = new CollectionManager();
        }
        return instance;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Получить коллекцию
     *
     * @return коллекция со всеми элементами
     */
    public static HashSet<Vehicle> getVehicleCollection() {
        return vehicleCollection;
    }

    /**
     * Установить коллекцию
     *
     */
    public void setVehicleCollection(HashSet<Vehicle> vehicleCollection) {
        this.vehicleCollection = vehicleCollection;
    }

    /**
     * Добавить элемент в коллекцию
     *
     */
    public static void addToCollection(Vehicle vehicle) {
        vehicleCollection.add(vehicle);
    }

    /**
     * Очистить коллекцию
     *
     */
    public static void clearCollection() {
        vehicleCollection.clear();
    }

    /**
     * Показать все элементы коллекции
     *
     */
    public String showCollection() {
        if (vehicleCollection.isEmpty()) {
            return "Collection is empty";
        }

        String result = vehicleCollection.stream()
                .map(Vehicle::toString)
                .collect(Collectors.joining("\n"));
        return result;
    }

    /**
     * Показать информацию о коллекции
     *
     */



    /**
     * Удалить элемент из коллекции по его ID
     *
     * @param
     */
    public static String removeById(Request request) throws NoElementException {
        if (vehicleCollection.isEmpty()) {
            return "Collection is empty";
        }
        long id = Long.parseLong(request.getKey());
        Optional<Vehicle> vehicleOptional = vehicleCollection.stream()
                .filter(vehicle -> vehicle.getId() == id)
                .findFirst();
        if(vehicleOptional.isPresent()) {
            vehicleCollection.remove(vehicleOptional.get());
            IdGenerator.removeId(id);
            return "Element with id " + id + " was successfully removed";
        } else {
            throw new NoElementException(id);
        }
    }


    public static String removeLower(Request request) {
        try {
            VehicleEnginePowerComparator vehicleEnginePowerComparator = new VehicleEnginePowerComparator();
            HashSet<Vehicle> vehicleCollection = CollectionManager.getVehicleCollection();

            long inputEl = Long.parseLong(request.getKey());

            Optional<Vehicle> referenceVehicleOptional = vehicleCollection.stream()
                    .filter(vehicle -> vehicle.getId() == inputEl)
                    .findFirst();

            if (!referenceVehicleOptional.isPresent()) {
                return "No element with this id";
            }
            Vehicle referenceVehicle = referenceVehicleOptional.get();

            HashSet<Vehicle> toRemove = vehicleCollection.stream()
                    .filter(vehicle -> vehicleEnginePowerComparator.compare(vehicle, referenceVehicle) < 0)
                    .collect(Collectors.toCollection(HashSet::new));

            if (toRemove.isEmpty()) {
                return "No elements to remove";
            } else {
                vehicleCollection.removeAll(toRemove);
                toRemove.forEach(vehicle -> IdGenerator.removeId(vehicle.getId()));
            }
        }catch (NumberFormatException e) {
            return "Please enter a digit";
        }catch (NullPointerException e) {
            return "No element with this id";
        }
        return "Removed successfully";
    }

    public static String GroupCountingByCreationDate(Request request) {
        HashSet<Vehicle> vehicleCollection = CollectionManager.getVehicleCollection();
        if (vehicleCollection.isEmpty()) {
            return "Collection is empty";
        } else {
            Map<Date, Long> groupedByCreatioonDate = vehicleCollection.stream()
                    .collect(Collectors.groupingBy(Vehicle::getCreationDate,Collectors.counting()));
            String result = groupedByCreatioonDate.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining("\n"));
            return result;
        }
    }

    public static String countByFuelType(Request request) {
        HashSet<Vehicle> vehicleCollection = CollectionManager.getVehicleCollection();
        String enterFuelType = request.getKey();

        long count = vehicleCollection.stream()
                .filter(vehicle -> vehicle.getFuelType().toString().equalsIgnoreCase(enterFuelType))
                .count();
        return ("Coincidences: " + count);
    }

    public static String countLessThenFuelType(Request request) {
        HashSet<Vehicle> vehicleCollection = CollectionManager.getVehicleCollection();
        String enterFuelType = request.getKey();

        long count = vehicleCollection.stream()
                .filter(vehicle -> !(vehicle.getFuelType().toString().equalsIgnoreCase(enterFuelType)))
                .count();
        return ("Do not match: " + count);
    }

    public static String addIfMax(Request request) {
        VehicleEnginePowerComparator comparator = new VehicleEnginePowerComparator();
        if(vehicleCollection.isEmpty() || vehicleCollection.stream().allMatch(vehicle -> comparator.compare(vehicle,request.getVehicle()) < 0)) {
            CollectionManager.addToCollection(request.getVehicle());
        } else {
            return "new vehicle has lower engine power!";
        }
        return "successfully added";
    }

    public static String updateId(Request request) {
        try {
            HashSet<Vehicle> vehicleCollection = CollectionManager.getVehicleCollection();
            long inputEl = Long.parseLong(request.getKey());
            if (vehicleCollection.isEmpty()) {
                return "Collection is empty";
            }

            Optional<Vehicle> vehicleToUpdate = vehicleCollection.stream()
                    .filter(vehicle -> vehicle.getId() == inputEl)
                    .findFirst();

            if (!vehicleToUpdate.isPresent()) {
                return "No element with id " + inputEl + " found in the collection";
            }

            Vehicle updatedVehicle = request.getVehicle();
            updatedVehicle.setIdForUpdate(inputEl);
            updatedVehicle.setCreationDate(vehicleToUpdate.get().getCreationDate());
            removeById(request);
            addToCollection(updatedVehicle);
            return "Vehicle with id " + inputEl + " was successfully updated";
        }catch (NumberFormatException e) {
            return "Please enter digit";
        }catch (NoElementException e) {
            return e.getMessage();
        }
    }





    }
