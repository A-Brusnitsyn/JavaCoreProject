package exceptions;

public class IncorrectAmountException extends RuntimeException {
    public IncorrectAmountException(String message) {
        super(message);
    }

    public IncorrectAmountException() {
        super("Некорректно указана сумма на счете!");
    }
}
