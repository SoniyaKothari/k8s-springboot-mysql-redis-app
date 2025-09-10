package com.pastelplanner.config;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String msg = new String(message.getBody());

        System.out.println("Received Redis message: " + msg + " on channel: " + channel);

        // TODO: Forward this message to your Phoenix backend or WebSocket
        // e.g., send via HTTP API, WebSocket, or other messaging protocol
    }
}
