package com.example.anonymizer.consumer;

import com.example.anonymizer.HttpLog;
import com.example.anonymizer.domain.AnonymizedHttpLog;
import com.example.anonymizer.persistence.LocalCache;
import com.example.anonymizer.processor.CapNProtoDecoder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaConsumerService {
    private final CapNProtoDecoder capNProtoDecoder;
    private final LocalCache localCache;

    public KafkaConsumerService(CapNProtoDecoder capNProtoDecoder, LocalCache localCache) {
        this.capNProtoDecoder = capNProtoDecoder;
        this.localCache = localCache;
    }

    @KafkaListener(topics = "http_log", groupId = "anonymizer-group")
    public void consume(ConsumerRecord<String, byte[]> record) {
        byte[] message = record.value();

        try {
            HttpLog.HttpLogRecord.Reader httpLogRecord = capNProtoDecoder.decode(message);
            AnonymizedHttpLog anonymizedHttpLog = new AnonymizedHttpLog(httpLogRecord);
            localCache.insert(anonymizedHttpLog);

        } catch (IOException e) {
            System.err.println("Error while decoding message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
