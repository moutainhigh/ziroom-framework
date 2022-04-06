package com.ziroom.framework.autoconfigure.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.ziroom.framework.autoconfigure.common.ZiRoomAutoConfigException;
import com.ziroom.framework.autoconfigure.property.PlaceholderHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SpringInjector {
  private static volatile Injector s_injector;
  private static final Object lock = new Object();
  private static final Log log = LogFactory.getLog(SpringInjector.class);
  private static Injector getInjector() {
    if (s_injector == null) {
      synchronized (lock) {
        if (s_injector == null) {
          try {
            s_injector = Guice.createInjector(new SpringModule());
          } catch (Throwable ex) {
            ZiRoomAutoConfigException exception = new ZiRoomAutoConfigException("Unable to initialize Apollo Spring Injector!", ex);
            log.error(exception);
            throw exception;
          }
        }
      }
    }

    return s_injector;
  }

  public static <T> T getInstance(Class<T> clazz) {
    try {
      return getInjector().getInstance(clazz);
    } catch (Throwable ex) {
      log.error(ex);
      throw new ZiRoomAutoConfigException(
          String.format("Unable to load instance for %s!", clazz.getName()), ex);
    }
  }

  private static class SpringModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(PlaceholderHelper.class).in(Singleton.class);
//      bind(ConfigPropertySourceFactory.class).in(Singleton.class);
//      bind(SpringValueRegistry.class).in(Singleton.class);
      //
//      bind(AutoBeanFieldRegistry.class).in(Singleton.class);
    }
  }
}
