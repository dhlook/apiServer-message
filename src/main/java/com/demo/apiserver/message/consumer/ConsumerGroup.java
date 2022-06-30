package com.demo.apiserver.message.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.demo.apiserver.message.KafkaSettings;
import com.demo.apiserver.message.domain.BaseDomain;
import com.demo.apiserver.message.service.TopicService;

@Component
public class ConsumerGroup {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerGroup.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private TopicService service;
    private ExecutorService executor;
    private int defaultBatchSize;
    private List<BaseDomain> list = new ArrayList<BaseDomain>();

    public ConsumerGroup() {
        this.consumer = null;
        this.topic = null;
    }
    
    final Runnable runConsumer = new Runnable() {
        public void run() {
            if (list.size() > 0) {
                service.messageList(list);
                list.clear();
            }
        }
    };
    
    public ConsumerGroup(KafkaSettings kafkaSettings, String topic, TopicService service, int defaultBatchSize) {
        consumer = new KafkaConsumer<>(createConsumerConfig(kafkaSettings));
        this.topic = topic;
        this.service = service;
        this.defaultBatchSize = defaultBatchSize;
        scheduler.scheduleAtFixedRate(runConsumer, 1, 1, TimeUnit.MINUTES);
    }

    public void run(int numThreads) {

         executor = Executors.newFixedThreadPool(numThreads);
         executor.submit(new Runnable() {
             public void run() {
            	
            	try {
            		consumer.subscribe(Collections.singletonList(topic));
            		
            		while (true) {
             			ConsumerRecords<String, String> consumerRecords = consumer.poll(1000);
             			consumerRecords.forEach(record -> {
    	 					BaseDomain domain = null;
    	 					try {
    							domain = mapper.readValue(record.value(), BaseDomain.class);
    							list.add(domain);
    							if (list.size() >= defaultBatchSize) {
    								service.messageList(list);
    								list.clear();
    							}
    						} catch (IOException e) {
    							logger.error(e.getMessage());
    						}
     					});
                 	}
            	} catch(Exception e) {
            		logger.info("ConsumerGroup Run Exception : " + e.getMessage());
            	} finally {
            		consumer.close();
            	}
         	}	
         });
    }
    
    public void shutdown() {
    	consumer.wakeup();
    }

    private static Properties createConsumerConfig(KafkaSettings kafkaSettings) {
    	Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSettings.getZookeeperConnect());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaSettings.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); 
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");    
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, kafkaSettings.getAutoCommitIntervalMs()); 
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, kafkaSettings.getFetchMessageMaxBytes()); 
        return props;
    }

}
