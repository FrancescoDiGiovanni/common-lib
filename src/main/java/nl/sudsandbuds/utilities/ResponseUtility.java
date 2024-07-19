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

    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildSuccessResponseEntity(String message, T payload) {
        Response<T> response = new Response<>();
        return new ResponseEntity<>(response.buildResponse(true, message, payload), HttpStatus.OK);
    }

    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildSuccessResponseEntity(String message, T payload, Logger log) {
        log.info(message);
        Response<T> response = new Response<>();
        return new ResponseEntity<>(response.buildResponse(true, message, payload), HttpStatus.OK);
    }

    @SuppressWarnings({"unused", "unchecked"})
    public static <T> ResponseEntity<Response<T>> buildErrorResponseEntityFromServiceException(ServiceHttpStatusException e) {
        Response<T> response = new Response<>();
        return new ResponseEntity<>(response.buildErrorResponse(e.getCode(), e.getResponseMessage()),
                e.getHttpStatus());
    }

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

    @SuppressWarnings({"unused", "unchecked"})
    public static <T> boolean isResponseOk(ResponseEntity<Response<T>> responseEntity) {
        Response<T> responseEntityBody = responseEntity.getBody();
        return responseEntity.getStatusCode() == HttpStatus.OK && responseEntityBody != null && responseEntityBody.isSuccess();
    }

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
}
