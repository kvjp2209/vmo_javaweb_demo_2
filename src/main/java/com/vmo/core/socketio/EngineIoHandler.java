package com.vmo.core.socketio;

import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoWebSocket;
import io.socket.parseqs.ParseQS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class EngineIoHandler implements HandshakeInterceptor, WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(EngineIoHandler.class);

    private static final String ATTRIBUTE_ENGINEIO_BRIDGE = "engineIo.bridge";
    private static final String ATTRIBUTE_ENGINEIO_QUERY = "engineIo.query";

    private final EngineIoServer mEngineIoServer;

    public EngineIoHandler(EngineIoServer engineIoServer) {
        mEngineIoServer = engineIoServer;
    }

    @RequestMapping(
            value = {"/engine.io/", "/socket.io/**"},
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
            headers = "Connection!=Upgrade")
    public void httpHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("using http request");
//        if (request.getSession(false) == null && !request.getSession().isNew()) {
//            response.sendError(400, "auth fail");
//        }
        mEngineIoServer.handleRequest(request, response);
    }

    /* HandshakeInterceptor */

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("beforeHandshake");
        attributes.put(ATTRIBUTE_ENGINEIO_QUERY, request.getURI().getQuery());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("afterHandshake");
    }

    /* WebSocketHandler */

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        final EngineIoSpringWebSocket webSocket = new EngineIoSpringWebSocket(webSocketSession);
        webSocketSession.getAttributes().put(ATTRIBUTE_ENGINEIO_BRIDGE, webSocket);
        mEngineIoServer.handleWebSocket(webSocket);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        ((EngineIoSpringWebSocket)webSocketSession.getAttributes().get(ATTRIBUTE_ENGINEIO_BRIDGE))
                .afterConnectionClosed(closeStatus);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        ((EngineIoSpringWebSocket)webSocketSession.getAttributes().get(ATTRIBUTE_ENGINEIO_BRIDGE))
                .handleMessage(webSocketMessage);
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        ((EngineIoSpringWebSocket)webSocketSession.getAttributes().get(ATTRIBUTE_ENGINEIO_BRIDGE))
                .handleTransportError(throwable);
    }

    private static final class EngineIoSpringWebSocket extends EngineIoWebSocket {

        private final WebSocketSession mSession;
        private final Map<String, String> mQuery;

        EngineIoSpringWebSocket(WebSocketSession session) {
            mSession = session;

            final String queryString = (String)mSession.getAttributes().get(ATTRIBUTE_ENGINEIO_QUERY);
            if (queryString != null) {
                mQuery = ParseQS.decode(queryString);
            } else {
                mQuery = new HashMap<>();
            }
        }

        /* EngineIoWebSocket */

        @Override
        public Map<String, String> getQuery() {
            return mQuery;
        }

        @Override
        public void write(String message) throws IOException {
            mSession.sendMessage(new TextMessage(message));
        }

        @Override
        public void write(byte[] message) throws IOException {
            mSession.sendMessage(new BinaryMessage(message));
        }

        @Override
        public void close() {
            try {
                mSession.close();
            } catch (IOException ignore) {
            }
        }

        /* WebSocketHandler */

        void afterConnectionClosed(CloseStatus closeStatus) {
            emit("close");
        }

        void handleMessage(WebSocketMessage<?> message) {
            if (message.getPayload() instanceof String || message.getPayload() instanceof byte[]) {
                emit("message", (Object) message.getPayload());
            } else {
                throw new RuntimeException(String.format(
                        "Invalid message type received: %s. Expected String or byte[].",
                        message.getPayload().getClass().getName()));
            }
        }

        void handleTransportError(Throwable exception) {
            emit("error", "write error", exception.getMessage());
        }
    }
}
