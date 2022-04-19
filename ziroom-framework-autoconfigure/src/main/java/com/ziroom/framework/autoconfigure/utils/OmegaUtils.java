package com.ziroom.framework.autoconfigure.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.env.Environment;

@UtilityClass
public class OmegaUtils {

    public static final String ENV_APPLICATION_NAME = "OMEGA_APPLICATION_NAME";
    public static final String ENV_ENV_NAME = "OMEGA_ENV_NAME";
    public static final String ENV_ENV_LEVEL = "OMEGA_ENV_LEVEL";

    public static String getApplicationName(Environment env) {
        return env.getProperty(ENV_APPLICATION_NAME);
    }

    public static String getRuntimeEnvName(Environment env) {
        return env.getProperty(ENV_ENV_NAME);
    }

    public static OmegaEnvLevel getRuntimeEnvLevel(Environment env) {
        return OmegaEnvLevel.byEnvValue(env.getProperty(ENV_ENV_LEVEL));
    }

}
