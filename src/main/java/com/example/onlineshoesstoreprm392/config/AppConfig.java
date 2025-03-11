package com.example.onlineshoesstoreprm392.config;

import com.example.onlineshoesstoreprm392.utils.PaymentWebsocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableWebSocket
public class AppConfig implements WebSocketConfigurer {

    @Bean(name = "taskExecutor")
    public Executor asyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // minimum number of thread
        executor.setMaxPoolSize(10); //maximum
        executor.setQueueCapacity(50); // so task co the xep hang
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new PaymentWebsocketHandler(), "/ws/payment").setAllowedOrigins("*");
    }
}
