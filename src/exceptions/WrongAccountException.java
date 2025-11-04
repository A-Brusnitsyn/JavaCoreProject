package exceptions;

public class WrongAccountException extends RuntimeException {
    public WrongAccountException(String message) {
        super(message);
    }

    public WrongAccountException() {
        super("Номер счета указан неверно!");
    }
}
