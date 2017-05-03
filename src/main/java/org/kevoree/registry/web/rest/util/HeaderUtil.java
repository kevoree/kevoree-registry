package org.kevoree.registry.web.rest.util;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-kevoreeRegistryApp-alert", message);
        headers.add("X-kevoreeRegistryApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("kevoreeRegistryApp." + entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("kevoreeRegistryApp." + entityName + ".updated", param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("kevoreeRegistryApp." + entityName + ".deleted", param);
    }

    public static HttpHeaders createFailureAlert(String key) {
        return HeaderUtil.createFailureAlert(key, Maps.newHashMap());
    }

    public static HttpHeaders createFailureAlert(String key, HashMap<String, String> params) {
        log.error("Failure alert created in headers (key={}, params={})", key, params);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-kevoreeRegistryApp-error", key);
        params.entrySet().forEach((param) -> headers.add("X-kevoreeRegistryApp-param-" + param.getKey(), param.getValue()));
        return headers;
    }
}
