package org.kevoree.registry.server.oauth.google;

/**
 * This is a simple holder to prevent calling too many parameters in one
 * function because it is not cool for readability
 * Created by leiko on 19/11/14.
 */
public class AuthAccess {

    public String accessToken;
    public String idToken;
    public String tokenType;

}