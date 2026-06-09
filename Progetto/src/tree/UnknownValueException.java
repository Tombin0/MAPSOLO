package tree;

public class UnknownValueException extends Exception {

    /* Eccezione lanciata quando la predizione riceve un valore non valido. */
    public UnknownValueException(String message) {
        super(message);
    }
}
