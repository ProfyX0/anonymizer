package com.example.anonymizer.processor;

import com.example.anonymizer.HttpLog;
import org.capnproto.MessageReader;
import org.capnproto.Serialize;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.Channels;

public class CapNProtoDecoder {
    public static HttpLog.HttpLogRecord.Reader decode(byte[] data) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        var channel = Channels.newChannel(inputStream);
        MessageReader messageReader = Serialize.read(channel);
        return messageReader.getRoot(HttpLog.HttpLogRecord.factory);
    }
}
