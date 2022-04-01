package com.ziroom.framework.autoconfigure.jdbc;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface MessageProducerManager {
  /**
   * @return the message producer
   */
  MessageProducer getProducer();
}
