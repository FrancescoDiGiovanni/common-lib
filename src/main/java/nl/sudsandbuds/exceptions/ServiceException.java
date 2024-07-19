package nl.sudsandbuds.exceptions;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private final String code;

    public ServiceException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

}
