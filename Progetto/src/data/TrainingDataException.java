package data;

public class TrainingDataException extends Exception {

    /* Eccezione lanciata quando ci sono errori nei dati di training. */
    public TrainingDataException(String message) {
        super(message);
    }

    /* Eccezione che include un messaggio e la causa sottostante. */
    public TrainingDataException(String message, Throwable cause) {
        super(message, cause);
    }

    /* Eccezione costruita a partire da un'eccezione esistente. */
    public TrainingDataException(Throwable cause) {
        super(cause);
    }
}
