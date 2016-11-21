package com.govac.institutii;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.awaitility.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import com.govac.institutii.kafka.Message;
import com.govac.institutii.kafka.Producer;

import static org.awaitility.Awaitility.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KafkaTest {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaTest.class);

    @Autowired
    private Producer producer;

    @Autowired
    private Environment env;

    private ConsumerConnector consumer;

    private String topic = "testTopic";

    private AtomicBoolean messageReceived;

    private String messageKey;

    @Before
    public void before() {
        messageKey = String.valueOf(System.currentTimeMillis());
        createAndStartConsumer();
    }

    /**
     * Checks that a message can be delivered and received to/from the Kafka
     * broker. Expects the message to be received within a reasonable time (
     * {@link Duration#TEN_SECONDS})
     */
    @Test
    public void testMessageDelivery() {
        messageReceived = new AtomicBoolean(false);

        Message testMessage = Message.create("Hello world" + messageKey, topic);
        producer.send(testMessage);

        await().atMost(Duration.TEN_SECONDS).untilTrue(messageReceived);
    }

    /**
     * Demo for sending a synchronous message
     * 
     * @throws Exception
     *             if something wrong is happening while waiting for the result
     *             - see {@link Future#get()}
     */
    @Test
    public void testSynchronousMessageDelivery() throws Exception {
        Message testMessage = Message.create("Sync message", topic);
        RecordMetadata result = producer.send(testMessage).get();

        assertEquals("Message was not delivered to the expected topic", topic, result.topic());
        LOG.info("Confirmation received for sending synchronous message. Offset:{}, Partition:{}, Timestamp:{} ",
                result.offset(), result.partition(), result.timestamp());
    }

    private void createAndStartConsumer() {
        consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        ExecutorService executor = Executors.newFixedThreadPool(1);

        for (final KafkaStream<byte[], byte[]> stream : streams) {
            executor.submit(new Runnable() {

                @Override
                public void run() {
                    ConsumerIterator<byte[], byte[]> it = stream.iterator();
                    while (it.hasNext()) {
                        String message = new String(it.next().message());
                        LOG.info("Received message " + message);
                        if (message.endsWith(messageKey)) {
                            messageReceived.set(true);
                        }
                    }
                }
            });
        }
        consumer.commitOffsets();
    }

    @After
    public void after() {
        consumer.shutdown();
    }

    private ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", env.getProperty("zookeeper.connect"));
        props.put("group.id", env.getProperty("group.id"));
        props.put("auto.offset.reset", env.getProperty("auto.offset.reset"));

        return new ConsumerConfig(props);
    }
}
