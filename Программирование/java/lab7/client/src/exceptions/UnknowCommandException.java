package exceptions;

/**
 * Обеспечивает исключение, если такой команды не существует
 *
 * @author zxc * @since 1.0
 */
public class UnknowCommandException extends Exception{

    /**
     * @param command неизвестная команда
     *
     * @author zxc     * @since 1.0
     */
    public UnknowCommandException(String command) {
        super("Unknown command: " + command + " use help for more info");
    }

}
