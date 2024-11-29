package com.example.anonymizer.domain;

import com.example.anonymizer.HttpLog;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class AnonymizedHttpLog {
    private final Timestamp timestamp;
    private final long resourceId;
    private final long bytesSent;
    private final long requestTimeMilli;
    private final int responseStatus;
    private final String cacheStatus;
    private final String method;
    private final String remoteAddr;
    private final String url;

    public AnonymizedHttpLog(HttpLog.HttpLogRecord.Reader original) {
        this.timestamp = new Timestamp(original.getTimestampEpochMilli());
        this.resourceId = original.getResourceId();
        this.bytesSent = original.getBytesSent();
        this.requestTimeMilli = original.getRequestTimeMilli();
        this.responseStatus = original.getResponseStatus();
        this.cacheStatus = original.getCacheStatus().toString();
        this.method = original.getMethod().toString();
        this.remoteAddr = anonymizeIP(original.getRemoteAddr().toString());
        this.url = original.getUrl().toString();
    }

    private LocalDateTime convertToDateTime(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }



    private String anonymizeIP(String ip) {
        String[] split = ip.split("\\.");
        split[3] = "X";
        return String.join(".", split);
    }

    public Timestamp getTimestampEpochMilli() {
        return timestamp;
    }

    public long getResourceId() {
        return resourceId;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public long getRequestTimeMilli() {
        return requestTimeMilli;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getCacheStatus() {
        return cacheStatus;
    }

    public String getMethod() {
        return method;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getUrl() {
        return url;
    }
}

