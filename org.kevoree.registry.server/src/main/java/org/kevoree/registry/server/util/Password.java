package org.kevoree.registry.server.util;

/**
 * Created by leiko on 27/11/14.
 */
public class Password {
    private final int iterations;
    private final String hash;
    private final String salt;

    public Password(int it, String hash, String salt) {
        this.iterations = it;
        this.hash = hash;
        this.salt = salt;
    }

    public int getIterations() {
        return iterations;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }
}