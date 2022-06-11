package es.omarall.store.document.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Builder
@Getter
@Setter
public class FieldErrorResource implements Serializable {
    /**
     * Field related to the error
     */
    private String target;
//    private String resource;
    /**
     * A more specific error code than was provided by the containing error.
     */
    private String code;

    /**
     * A human-readable representation of the error.
     */
    private String message;
}
