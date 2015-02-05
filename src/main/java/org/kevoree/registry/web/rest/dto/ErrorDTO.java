package org.kevoree.registry.web.rest.dto;

/**
 * Created by leiko on 04/02/15.
 */
public class ErrorDTO {

    private String message;

    public ErrorDTO(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorDTO{message='"+message+"'}";
    }
}
