package managers;

import Collections.Coordinates;
import Collections.FuelType;
import Collections.Vehicle;
import Collections.VehicleType;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorWritingException;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.DateTimeException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Класс, отвечающий за сериализацию и десериализацию в XML и обратно в коллекцию
 *
 * @author zxc
 * @since 1.0
 */
public class FileManager {
    private static FileManager instance;
    private static String filepath;
    private final XStream xStream;

    /**
     * Базовый конструктор с настройкой тэгов для XML файла и настройками безопасности
     *
     * @param filepath путь к файлу
     * @since 1.0
     */
    public FileManager(String filepath) {
        this.filepath = filepath;
        xStream = new XStream(new DomDriver());
        xStream.registerConverter(new fexibleDateConverter());

        xStream.alias("Coordinates", Coordinates.class);
        xStream.alias("FuelType", FuelType.class);
        xStream.alias("Vehicle", Vehicle.class);
        xStream.alias("VehicleType", VehicleType.class);
        xStream.alias("set", HashSet.class);

        xStream.setMode(XStream.NO_REFERENCES);
        xStream.addPermission(NoTypePermission.NONE);
        xStream.addPermission(NullPermission.NULL);
        xStream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xStream.allowTypeHierarchy(HashSet.class);
        xStream.allowTypeHierarchy(String.class);
        xStream.ignoreUnknownElements();
    }

    public static synchronized FileManager getInstance(String filepath) {
        if (instance == null) {
            instance = new FileManager(filepath);
        }
        return instance;
    }
    /**
     * Метод для чтения данных из файла, проверки их валидности и записи в коллекцию
     *
     * @since 1.0
     */
    public HashSet<Vehicle> readCollection() {
        if (!filepath.isEmpty()) {
            try (Scanner FileScanner = new Scanner(new File(filepath))) {
                xStream.setMode(XStream.NO_REFERENCES);
                xStream.addPermission(NoTypePermission.NONE);
                xStream.addPermission(NullPermission.NULL);
                xStream.addPermission(PrimitiveTypePermission.PRIMITIVES);
                xStream.allowTypeHierarchy(HashSet.class);
                xStream.allowTypeHierarchy(String.class);
                xStream.ignoreUnknownElements();
                xStream.registerConverter(new fexibleDateConverter());
                xStream.allowTypes(new Class[]{java.util.HashSet.class, Vehicle.class});
                StringBuilder xml = new StringBuilder();
                while (FileScanner.hasNext()) {
                    xml.append(FileScanner.nextLine());
                }

                HashSet<Vehicle> vehicleStart = (HashSet<Vehicle>) xStream.fromXML(xml.toString());
                Validator validator = new Validator(vehicleStart);
                HashSet<Vehicle> vehicleHashSet = new HashSet<>();
                vehicleHashSet.addAll(validator.validateFile());
                return vehicleHashSet;
            } catch (StreamException e) {
                System.out.println("Error in xml teg");
                Logger.getLogger(FileManager.class.getName()).warning("Error in xml teg");
                System.exit(1);
            }
            catch (ConversionException e) {
                if (e.getCause() instanceof NumberFormatException) {
                    Logger.getLogger(FileManager.class.getName()).warning("Collection from file not loaded");
                    System.out.println("Error: String instead of number.");
                    System.out.println("Collection from file not loaded, check the source file");
                    System.exit(1);
                }
                if (e.getCause() instanceof IllegalArgumentException) {
                    Logger.getLogger(FileManager.class.getName()).warning("Collection from file not loaded");
                    System.out.println("Error: An incorrect value was entered in VehicleType or FuelType");
                    System.out.println("Collection from file not loaded");
                    System.out.println("Check the source file and enter one of the values\n" +
                            "for VehicleType: ( PLANE, SHIP, BICYCLE, MOTORCYCLE )\n" +
                            "for FuelType: ( KEROSENE, MANPOWER, NUCLEAR )");
                    System.exit(1);
                }
                if (e.getCause() instanceof ErrorWritingException) {
                    Logger.getLogger(FileManager.class.getName()).warning("Collection from file not loaded");
                    System.out.println("Collection from file not loaded");
                    System.out.println("Error: Incorrect date in element");
                    System.out.println("Check the date for compliance with the format: yyyy-MM-dd HH:mm:ss.SSS timezone");
                    System.exit(1);
                }
            }
            catch (FileNotFoundException exception) {
                Logger.getLogger(FileManager.class.getName()).warning("File was not found");
                System.out.println("File was not found");
                System.exit(1);
            }
            catch (IllegalStateException e) {
                Logger.getLogger(FileManager.class.getName()).warning("IllegalState");
                System.out.println("Unexpected error");
                System.exit(1);
            }
            catch (NullPointerException e) {
                Logger.getLogger(FileManager.class.getName()).warning("No collection in file");
                System.out.println("Can't find a collection in file");
                System.exit(1);
            }
            catch (NoSuchElementException e) {
                Logger.getLogger(FileManager.class.getName()).warning("File is empty");
                System.out.println("File is empty");
                System.exit(1);
            }
        } else System.out.println("Filename is wrong");
        return new HashSet<>();

    }

    /**
     * Метод для записи коллекции в XML файл
     *
     * @since 1.0
     */
    public void writeCollection(HashSet<Vehicle> vehicle) {
        if (!filepath.isEmpty()) {
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filepath), StandardCharsets.UTF_8))){

                printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
                String xml = xStream.toXML(new HashSet<>(vehicle));
                printWriter.write(xml);


            } catch (IOException e) {
                Logger.getLogger(FileManager.class.getName()).warning("File is a directory or can't be opened");
                System.out.println("File is a directory or can't be opened");
            }
        } else System.out.println("Filename is wrong");
    }

}
