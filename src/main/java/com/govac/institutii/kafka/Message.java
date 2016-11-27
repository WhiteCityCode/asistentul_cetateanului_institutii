package com.govac.institutii.kafka;

import java.nio.charset.Charset;

import static org.apache.commons.lang.Validate.notNull;

/**
 * Describes a message sent to Kafka.
 *
 */
public class Message {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    /**
     * The actual message value.
     */
    private final byte[] value;
    /**
     * The key used by partitioner to compute the partition destination.
     */
    private final byte[] key;

    /**
     * The topic this message should be sent on
     */
    private final String topic;

    /**
     * Creates a Message from provided string value. This implementation will
     * use the value as the key by default.
     *
     * @param value
     *            of the message.
     * @return Message instance with both key and value set.
     */
    public static Message create(final String value, final String topic) {
        notNull(value);
        final byte[] bytes = value.getBytes(DEFAULT_CHARSET);
        return new Message(bytes, bytes, topic);
    }

    /**
     * @param key
     *            associated with the message which is used by partitioner.
     * @param value
     *            the actual message value.
     */
    private Message(final byte[] key, final byte[] value, String topic) {
        notNull(value);
        this.key = key;
        this.value = value;
        this.topic = topic;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getKey() {
        return key;
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return new String(value, DEFAULT_CHARSET);
    }
}
