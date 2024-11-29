package com.example.anonymizer.persistence;

import com.example.anonymizer.domain.AnonymizedHttpLog;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ClickhouseService {

    @Value("${clickhouse.url}")
    private String clickhouseUrl;

    private final LocalCache localCache;

    public ClickhouseService(LocalCache localCache) {
        this.localCache = localCache;
    }

    @Scheduled(fixedRate = 65000)
    public void insertData() {
        if (localCache.isEmpty()) {
            return;
        }

        List<AnonymizedHttpLog> records = localCache.getAll();
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO http_log (timestamp, resource_id, bytes_sent, request_time_milli, response_status, cache_status, method, remote_addr, url) VALUES ");

        for (int i = 0; i < records.size(); i++) {
            AnonymizedHttpLog record = records.get(i);
            queryBuilder.append(String.format(
                    "(%d, %d, %d, %d, %d, '%s', '%s', '%s', '%s')%s",
                    record.getTimestampEpochMilli().getTime(),
                    record.getResourceId(),
                    record.getBytesSent(),
                    record.getRequestTimeMilli(),
                    record.getResponseStatus(),
                    record.getCacheStatus(),
                    record.getMethod(),
                    record.getRemoteAddr(),
                    record.getUrl(),
                    i < records.size() - 1 ? "," : ""
            ));
        }

        sendHttpPost(queryBuilder.toString(), records);
    }

    private void sendHttpPost(String query, List<AnonymizedHttpLog> records) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(clickhouseUrl);
            httpPost.setEntity(new StringEntity(query, StandardCharsets.UTF_8));

            client.execute(httpPost);
            System.out.println("Batch inserted " + records.size() + " records");
        } catch (Exception e) {
            e.printStackTrace();
            localCache.returnAll(records);
        }
    }
}

