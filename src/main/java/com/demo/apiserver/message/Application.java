package com.demo.apiserver.message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.demo.apiserver.message.consumer.ConsumerGroup;
import com.demo.apiserver.message.service.TopicService;

@EnableCaching
@SpringBootApplication
public class Application implements ApplicationRunner {

    @Autowired
    private KafkaSettings kafkaSettings;
    @Autowired
    private TopicService service;

    @Value("${default.db.insert.batch.size}")
    int defaultBatchSize;
    @Value("${es.file.insert.batch.size}")
    int esFileBatchSize;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

         for (String topic : kafkaSettings.getTopicList()) {
             new ConsumerGroup(kafkaSettings, topic, service, defaultBatchSize).run(kafkaSettings.getConsumerThreadNum());
         }

    }
}
