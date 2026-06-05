package pl.example.cs2.activity.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.example.cs2.common.events.PlayerJoinedTeamEvent;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final SimpMessagingTemplate messagingTemplate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ActivityController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/team-move")
    public void recordTeamMove(@RequestBody PlayerJoinedTeamEvent event) {
        String message = String.format("[%s] Player %s moved to team %s",
                event.occurredAt().atZone(java.time.ZoneId.systemDefault()).format(FORMATTER),
                event.username(),
                event.teamName());
        
        messagingTemplate.convertAndSend("/topic/feed", message);
        System.out.println("Broadcasted activity: " + message);
    }
}
