package nl.sudsandbuds.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import nl.sudsandbuds.exceptions.ServiceHttpStatusException;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtility {
    private static final String ERROR_CODE_MESSAGE_FORMAT = "{}: {}";
    private static final String FEIGN_CLIENT_ERROR_CODE_NUMBER = "0111";
    private static final String FEIGN_CLIENT_ERROR_RESPONSE_MESSAGE = "Error during communication with another service through FeignClient";

    /**
     * Response utility that builds a success response without logging
     * @param message ok message
     * @param payload data to pass in the body of the response
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildSuccessResponseEntity(String message, T payload) {
        Response<T> response = new Response<>();
        return new ResponseEntity<>(response.buildResponse(true, message, payload), HttpStatus.OK);
    }

    /**
     * Response utility that builds a success response and logs the message in the server console
     * @param message ok message
     * @param payload data to pass in the body of the response
     * @param log slf4j logger
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildSuccessResponseEntity(String message, T payload, Logger log) {
        log.info(message);
        Response<T> response = new Response<>();
        return new ResponseEntity<>(response.buildResponse(true, message, payload), HttpStatus.OK);
    }

    /**
     * Response utility that builds a error response and logs the message in the server console
     * @param code status code of the error
     * @param httpStatus http status of the error
     * @param errorMessage error message
     * @param payload payload of the error to pass
     * @param log slf4j logger
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildErrorResponseEntity(String code, HttpStatus httpStatus, String errorMessage, T payload, Logger log) {
        log.info(ERROR_CODE_MESSAGE_FORMAT, code, errorMessage);
        Response<T> response = new Response<>();
        return new ResponseEntity<>(response.buildErrorResponse(code, errorMessage, payload),
                httpStatus);
    }

    /**
     * Response utility that builds a error response from a service exception
     * @param e service http status exception thrown by service exception
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildErrorResponseEntityFromServiceException(ServiceHttpStatusException e) {
        Response<T> response = new Response<>();
        return new ResponseEntity<>(response.buildErrorResponse(e.getCode(), e.getResponseMessage()),
                e.getHttpStatus());
    }

    /**
     * Response utility that builds a error response from a service exception
     * @param e service http status exception thrown by service exception
     * @Param log slf4j logger
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildErrorResponseEntityFromServiceException(ServiceHttpStatusException e, Logger log) {
        log.error(ERROR_CODE_MESSAGE_FORMAT, e.getCode(), e.getMessage(), e);
        Response<T> response = new Response<>();
        return new ResponseEntity<>(
                response.buildErrorResponse(e.getCode(), e.getMessage()),
                e.getHttpStatus()
        );
    }

    /**
     * Response utility that builds a error response from a service
     * @param responseEntity response from a service
     * @param log slf4j logger
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T, U> ResponseEntity<Response<T>> buildResponseEntityErrorFromService(ResponseEntity<Response<U>> responseEntity, Logger log) {
        Response<U> input = responseEntity.getBody();
        Response<T> output = new Response<>();
        if ( input != null && input.getError() != null ) {
            log.error(ERROR_CODE_MESSAGE_FORMAT, input.getError().getCode(), input.getError().getMessage());
            output.buildErrorResponse(input.getError().getCode(), input.getError().getMessage());
        } else {
            log.error(input.getMessage());
            output.buildResponse(false, input.getMessage(), null);
        }

        return new ResponseEntity<>(output, responseEntity.getStatusCode());
    }

    /**
     * Utility method that checks if the response is ok or ko
     * @param responseEntity response from a call to another service
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> boolean isResponseOk(ResponseEntity<Response<T>> responseEntity) {
        Response<T> responseEntityBody = responseEntity.getBody();
        return responseEntity.getStatusCode() == HttpStatus.OK && responseEntityBody != null && responseEntityBody.isSuccess();
    }

    /**
     * Response utility that builds a error response from a failure of a feignclient call
     * @param e exception from feign client
     * @param serviceCode servicecode fixed to 0111
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildResponseEntityFromFeignClientException(FeignException e, String serviceCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        Response<T> errorResponse = new Response<>();
        try {
            errorResponse = objectMapper.readValue(e.contentUTF8(), Response.class);
        } catch (Exception e1) {
            HttpStatus httpStatus = null;
            if ( e.status() == -1 )
                httpStatus = HttpStatus.valueOf(500);
            else
                httpStatus = HttpStatus.valueOf(e.status());

            return new ResponseEntity<>(errorResponse.buildErrorResponse(serviceCode + FEIGN_CLIENT_ERROR_CODE_NUMBER, FEIGN_CLIENT_ERROR_RESPONSE_MESSAGE), httpStatus);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.status()));

    }

    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildResponseEntityFromFeignClientException(FeignException e, String serviceCode, Logger log) {
        log.error(e.getMessage(), e);
        ObjectMapper objectMapper = new ObjectMapper();
        Response<T> errorResponse = new Response<>();
        try {
            errorResponse = objectMapper.readValue(e.contentUTF8(), Response.class);
        } catch (Exception e1) {
            HttpStatus httpStatus = null;
            if ( e.status() == -1 )
                httpStatus = HttpStatus.valueOf(500);
            else
                httpStatus = HttpStatus.valueOf(e.status());

            return new ResponseEntity<>(errorResponse.buildErrorResponse(serviceCode + FEIGN_CLIENT_ERROR_CODE_NUMBER, FEIGN_CLIENT_ERROR_RESPONSE_MESSAGE), httpStatus);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.status()));

    }
}
