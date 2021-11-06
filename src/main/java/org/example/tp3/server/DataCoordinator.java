package org.example.tp3.server;

import org.example.tp3.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DataCoordinator {

    private final List<Message> messages = new ArrayList<>();
    private final AtomicInteger iterationsCount = new AtomicInteger(0);

    public void addMessage(Message message) {
        messages.add(message);
    }

    public Message getLastMessage() {
        return messages.size() == 0 ? null : messages.get(messages.size() - 1);
    }

    public int getIterationsCount() {
        return iterationsCount.get();
    }

    public void incrementIterationsCount() {
        iterationsCount.incrementAndGet();
    }

    public String getAnswer() {
        if (messages.size() == 0) {
            throw new IllegalStateException("Messages collection is empty");
        }

        return messages.stream().map(Message::getContent).collect(Collectors.joining("\n"));
    }

    public State getState(int clientNumber) {
        if (getIterationsCount() / 4 == ServerApplication.FULL_ITERATIONS_COUNT) {
            return State.EXIT;
        }
        if (getIterationsCount() % 4 == clientNumber - 1) {
            return State.WORK;
        }
        return State.WAIT;
    }
}
