package org.kevoree.registry.server.util;

import com.eclipsesource.json.JsonObject;

/**
 * Created by leiko on 27/11/14.
 */
public class ResponseError {

    private String name;
    private String message;

    public ResponseError(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public ResponseError(String msg) {
        this("error", msg);
    }

    @Override
    public String toString() {
        return new JsonObject().add(this.name, this.message).toString();
    }
}
