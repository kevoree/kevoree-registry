package org.kevoree.registry.config;

/**
 * Application constants.
 */
public final class Constants {

    //Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[a-z0-9]*$";
    public static final String TDEF_NAME_REGEX = "^[A-Z][\\w]*$";
    public static final String DU_VERSION_REGEX = "^(?:[0-9]+)\\.(?:[0-9]+)\\.(?:[0-9]+)(?:-(?:[0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?(?:\\+[0-9A-Za-z-]+)?$";
    public static final String NS_NAME_REGEX = "^[a-z0-9]+$";
    // Spring profiles for development, test and production, see http://jhipster.github.io/profiles/
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";

    private Constants() {
    }

}
