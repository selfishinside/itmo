package ru.itmo.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
@ServerEndpoint("/updates/dragons")
public class DragonUpdateEndpoint {
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    public void broadcastUpdate() {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText("update");
            }
        });
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
    }
}
