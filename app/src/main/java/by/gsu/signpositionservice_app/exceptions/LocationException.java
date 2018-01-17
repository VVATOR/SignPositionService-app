package by.gsu.signpositionservice_app.exceptions;

public class LocationException extends Exception {

    private String information = "Permission denied for operation 'get location'";

    public LocationException() {
        super();

    }

    public LocationException(String message) {
        super(message);
        this.information = message;
    }

    public LocationException(String message, Exception cause) {
        super(message, cause);
        this.information = message;
    }

    public LocationException(Exception cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return information + "\t=>\t " + getMessage();
    }
}
