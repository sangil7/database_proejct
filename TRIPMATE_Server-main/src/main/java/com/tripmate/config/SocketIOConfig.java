package com.tripmate.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("0.0.0.0"); // 모든 네트워크 인터페이스 허용
        config.setPort(9092);          // 사용할 포트 번호
        return new SocketIOServer(config);
    }
}
