package managers;

import Collections.FuelType;
import Collections.Vehicle;
import Collections.VehicleType;
import Generators.IdGenerator;
import exceptions.NoElementException;
import exceptions.WrongArgumentException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Класс для проверки на валидность полей объекта Vehicle и для проверки входных данных из файла
 *
 * @author zxc
 * @since 1.0
 */
public class Validator {
    HashSet<Vehicle> vehicleList;

    public Validator(HashSet<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    /**
     * Проверяет корректность полей элементов из входного файла, удаляет элемент при несоответствии
     *
     * @see Collections.Vehicle
     */
    public HashSet<Vehicle> validateFile() {
        for(Iterator<Vehicle> iterator = vehicleList.iterator(); iterator.hasNext();) {
            Vehicle vehicle = iterator.next();
            if((vehicle.getId() > 0) && ((IdGenerator.idUnique(vehicle.getId())))) {
                IdGenerator.addId(vehicle.getId());
            }else {
                iterator.remove();
                System.out.println("id must be greater than 0, unique and as an integer");
                System.out.println("element from the input file was removed");
            }
            if(vehicle.getName() == null || vehicle.getName().isEmpty()) {
                iterator.remove();
                System.out.println("name cannot be empty or null");
                System.out.println("element from the input file was removed");
            }
            if(vehicle.getCoordinates() == null) {
                iterator.remove();
                System.out.println("coordinates cannot be null");
                System.out.println("element from the input file was removed");
            }

            if (vehicle.getEnginePower() == null || vehicle.getEnginePower() <= 0) {
                iterator.remove();
                System.out.println("engine power cannot be less then 0 and as an integer");
                System.out.println("element from the input file was removed");
            }

            if(vehicle.getCoordinates().getX() <= -952) {
                iterator.remove();
                System.out.println("X cannot be less then -952 and as an integer");
                System.out.println("element from the input file was removed");
            }
            if(vehicle.getCoordinates().getY() <= -109) {
                iterator.remove();
                System.out.println("Y cannot be less then -109 and as an integer");
                System.out.println("element from the input file was removed");
            }
        }
        return vehicleList;
    }
}
