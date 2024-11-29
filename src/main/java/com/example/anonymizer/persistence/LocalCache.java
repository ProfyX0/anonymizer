package com.example.anonymizer.persistence;

import com.example.anonymizer.HttpLog;
import com.example.anonymizer.domain.AnonymizedHttpLog;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class LocalCache {
    private final ConcurrentLinkedQueue<AnonymizedHttpLog> queue;

    public LocalCache() {
        queue = new ConcurrentLinkedQueue<>();
    }
    
    public void insert(AnonymizedHttpLog record) {
        queue.add(record);
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }

    public List<AnonymizedHttpLog> getAll() {
        List<AnonymizedHttpLog> list = new ArrayList<>();
        AnonymizedHttpLog record;
        while ((record = queue.poll()) != null) {
            list.add(record);
        }
        return list;
    }

    public void returnAll(List<AnonymizedHttpLog> list) {
        queue.addAll(list);
    }
}
