package com.ziroom.framework.autoconfigure.utils;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.Set;

public enum OmegaEnvLevel {


    LOCAL(null),


    DAILY("daily"),

    QA("qua"),

    STABLE("stable"),

    PRE("pre"),

    PRODUCTION("prod"),

    UNKNOWN("UNKNOWN");

    @Getter
    private final String envValue;

    OmegaEnvLevel(String envValue) {
        this.envValue = envValue;
    }

    public static OmegaEnvLevel byEnvValue(String envValue) {
        if (StringUtils.isBlank(envValue)) {
            return OmegaEnvLevel.LOCAL;
        }

        for (OmegaEnvLevel level : VALID_ENV) {
            if (level.getEnvValue().equals(envValue)) {
                return level;
            }
        }

        return OmegaEnvLevel.UNKNOWN;
    }

    public static final Set<OmegaEnvLevel> VALID_ENV = EnumSet.of(DAILY, QA, STABLE, PRE, PRODUCTION);
}
