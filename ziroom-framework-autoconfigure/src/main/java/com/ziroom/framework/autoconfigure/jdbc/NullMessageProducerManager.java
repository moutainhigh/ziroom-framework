package com.ziroom.framework.autoconfigure.jdbc;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class NullMessageProducerManager implements MessageProducerManager {
  private static final MessageProducer producer = new NullMessageProducer();

  @Override
  public MessageProducer getProducer() {
    return producer;
  }
}
