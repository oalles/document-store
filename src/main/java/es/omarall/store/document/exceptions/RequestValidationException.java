package es.omarall.store.document.exceptions;

import es.omarall.store.document.dto.ErrorInfo;
import es.omarall.store.document.dto.FieldErrorResource;
import lombok.Getter;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RequestValidationException extends RuntimeException {

    private ErrorInfo errorInfo;

    public RequestValidationException(ErrorInfo errorInfo) {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
    }

    public RequestValidationException(String message, List<FieldErrorResource> fieldErrorResources) {
        super(message);
        ErrorInfo error = new ErrorInfo();
        error.setCode("InvalidRequest");
        error.setMessage("The request has invalid content");
        error.setDetails(fieldErrorResources);
        this.errorInfo = error;
    }

    public RequestValidationException(String message, Errors errors) {
        this(message, errors.getFieldErrors().stream()
                .map(fieldError ->
                        FieldErrorResource.builder()
                                .code("InvalidField")
                                .target(fieldError.getField())
                                .message(fieldError.getDefaultMessage()).build()).collect(Collectors.toList()));
    }

    /**
     * Constructor for just one failing fields
     *
     * @param message
     * @param field
     */
    public RequestValidationException(String message, String field) {
        this(message, Arrays.asList(FieldErrorResource.builder()
                .code("InvalidField")
                .message(message)
                .target(field)
                .build()));
    }


    public RequestValidationException(String message) {
        this(message, Arrays.asList());
        this.errorInfo.setMessage(message);
    }
}
