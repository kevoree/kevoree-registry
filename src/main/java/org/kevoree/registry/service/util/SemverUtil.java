package org.kevoree.registry.service.util;

import com.github.zafarkhaja.semver.Version;

/**
 * SemVer util
 */
public class SemverUtil {

    public static int compare(Version v0, Version v1) {
        if (v0.greaterThan(v1)) {
            return 1;
        } else if (v0.lessThan(v1)) {
            return -1;
        } else {
            return 0;
        }
    }
}
