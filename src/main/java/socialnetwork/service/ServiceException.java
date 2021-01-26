package socialnetwork.service;

public class ServiceException extends RuntimeException{
    private String message;

    public ServiceException() {}

    public ServiceException(String message) {
        super(message);
        this.message = message;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getMessage() {
        return message;
    }
}
