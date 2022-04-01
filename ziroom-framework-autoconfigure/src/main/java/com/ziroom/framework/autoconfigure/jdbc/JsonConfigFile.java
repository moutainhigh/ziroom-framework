package com.ziroom.framework.autoconfigure.jdbc;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class JsonConfigFile extends PlainTextConfigFile {
  public JsonConfigFile(String namespace,
                        ConfigRepository configRepository) {
    super(namespace, configRepository);
  }

  @Override
  public ConfigFileFormat getConfigFileFormat() {
    return ConfigFileFormat.JSON;
  }
}
