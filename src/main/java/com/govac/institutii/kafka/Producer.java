package com.govac.institutii.kafka;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    private final KafkaProducer<byte[], byte[]> producer;

    public Producer(Properties kafkaProperties) throws IOException {
        producer = new KafkaProducer<byte[], byte[]>(kafkaProperties);
    }

    public Future<RecordMetadata> send(Message message) {
        return producer.send(new ProducerRecord<byte[], byte[]>(message.getTopic(), message.getKey(), message.getValue()),
                callBack(message));
    }

    private Callback callBack(Message message) {
        return new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    LOG.error("Failed to deliver message to Kafka {}", exception);
                } else {
                    LOG.debug("Delivered message to Kafka: {}", message);
                }
            }
        };
    }

    /**
     * Ensure the producer is closed when the JVM is shutdown
     */
    @SuppressWarnings("unused")
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Closing KafkaProducer");
                producer.close();
            }
        });
    }

}
