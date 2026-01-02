package ma.fetheddine.emsibot.web;

import ma.fetheddine.emsibot.agents.AIAgent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
public class ChatController {
    private AIAgent aiAgent;
    public ChatController(AIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }
    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(String query) {
        return aiAgent.askAgent(query);
    }
}