package com.ziroom.framework.autoconfigure.jdbc;


import com.ziroom.framework.autoconfigure.common.CommonMixUtils;

public final class EnvUtils {
  
  public static Env transformEnv(String envName) {
    if (CommonMixUtils.isBlank(envName)) {
      return Env.UNKNOWN;
    }
    switch (envName.trim().toUpperCase()) {
      case "LPT":
        return Env.LPT;
      case "FAT":
      case "FWS":
        return Env.FAT;
      case "UAT":
        return Env.UAT;
      case "PRO":
      case "PROD": //just in case
        return Env.PRO;
      case "DEV":
        return Env.DEV;
      case "LOCAL":
        return Env.LOCAL;
      case "TOOLS":
        return Env.TOOLS;
      case "TEST":
    	  return Env.TEST;
      case "Q":
      case "QUASI":
    	  return Env.QUASI;
      default:
        return Env.UNKNOWN;
    }
  }
}
