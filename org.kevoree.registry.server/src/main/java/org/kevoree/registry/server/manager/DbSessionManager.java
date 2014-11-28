package org.kevoree.registry.server.manager;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.*;

import java.util.Set;

/**
 * TODO remove proxy and impl Session managing with Db
 * Created by leiko on 28/11/14.
 */
public class DbSessionManager implements SessionManager {

    private InMemorySessionManager proxy;

    public DbSessionManager(String name) {
        this.proxy = new InMemorySessionManager(name);
    }

    @Override
    public String getDeploymentName() {
        return this.proxy.getDeploymentName();
    }

    @Override
    public void start() {
        this.proxy.start();
    }

    @Override
    public void stop() {
        this.proxy.stop();
    }

    @Override
    public Session createSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        return this.proxy.createSession(serverExchange, sessionCookieConfig);
    }

    @Override
    public Session getSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        return this.proxy.getSession(serverExchange, sessionCookieConfig);
    }

    @Override
    public Session getSession(String sessionId) {
        return this.proxy.getSession(sessionId);
    }

    @Override
    public void registerSessionListener(SessionListener listener) {
        this.proxy.registerSessionListener(listener);
    }

    @Override
    public void removeSessionListener(SessionListener listener) {
        this.proxy.removeSessionListener(listener);
    }

    @Override
    public void setDefaultSessionTimeout(int timeout) {
        this.proxy.setDefaultSessionTimeout(timeout);
    }

    @Override
    public Set<String> getTransientSessions() {
        return this.proxy.getTransientSessions();
    }

    @Override
    public Set<String> getActiveSessions() {
        return this.proxy.getActiveSessions();
    }

    @Override
    public Set<String> getAllSessions() {
        return this.proxy.getAllSessions();
    }
}
