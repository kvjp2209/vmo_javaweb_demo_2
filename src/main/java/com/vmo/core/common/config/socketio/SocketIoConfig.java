package com.vmo.core.common.config.socketio;

import com.vmo.core.socketio.EngineIoHandler;
import io.socket.engineio.server.EngineIoServer;
import io.socket.socketio.server.SocketIoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@ComponentScan({
        "com.vmo.core.socketio"
})
public class SocketIoConfig implements WebSocketConfigurer {
    @Autowired
    private EngineIoHandler mEngineIoHandler;

//    public SocketIoConfig(EngineIoHandler engineIoHandler) {
//        mEngineIoHandler = engineIoHandler;
//    }

    @Bean
    public EngineIoServer engineIoServer() {
        return new EngineIoServer();
    }

    @Bean
    public SocketIoServer socketIoServer(EngineIoServer engineIoServer) {
        return new SocketIoServer(engineIoServer);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mEngineIoHandler, "/engine.io/", "/socket.io/**")
                .addInterceptors(mEngineIoHandler);
    }
}
