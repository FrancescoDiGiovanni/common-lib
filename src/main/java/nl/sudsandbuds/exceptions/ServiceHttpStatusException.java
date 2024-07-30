package nl.sudsandbuds.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceHttpStatusException extends ServiceException {
    private final HttpStatus httpStatus;
    private final String responseMessage;

    public ServiceHttpStatusException(String code, String message, Throwable cause, HttpStatus httpStatus) {
        super(code, message, cause);
        this.httpStatus = httpStatus;
        this.responseMessage = "";

    }

    public ServiceHttpStatusException(String code, String message, HttpStatus httpStatus) {
        super(code, message);
        this.httpStatus = httpStatus;
        this.responseMessage = "";
    }

}
