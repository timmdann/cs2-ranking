package pl.example.cs2.match.event;

import org.springframework.stereotype.Component;
import pl.example.cs2.common.events.MatchFinishedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class InMemoryEventBus {

    private final List<Consumer<MatchFinishedEvent>> handlers = new ArrayList<>();

    public void subscribe(Consumer<MatchFinishedEvent> handler) {
        handlers.add(handler);
    }

    public void publish(MatchFinishedEvent event) {
        handlers.forEach(h -> h.accept(event));
    }
}
