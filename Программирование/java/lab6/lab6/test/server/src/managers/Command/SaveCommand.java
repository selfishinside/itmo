package managers.Command;

import exceptions.WrongArgumentException;
import managers.CollectionManager;
import managers.FileManager;
import managers.Receiver;
import system.Request;



/**
 * Данная команда сохраняет коллекцию в XML файл
 *
 * @see BaseCommand
 * @author zxc
 * @since 1.0
 */
public class SaveCommand implements BaseCommand{
    @Override
    public String execute(Request request) throws Exception{
        try {
            return Receiver.saveData(request);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String getName() {
        return "save ";
    }

    @Override
    public String getDescription() {
        return "Save collection to xml file";
    }
}
