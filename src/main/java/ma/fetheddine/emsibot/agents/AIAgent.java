package ma.fetheddine.emsibot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

import java.util.Arrays;


@Component
public class AIAgent {
    private ChatClient chatClient;

    public AIAgent(ChatClient.Builder builder,
                   ChatMemory memory, ToolCallbackProvider tools) {
        System.out.println("Initializing AIAgent...");
        Arrays.stream(tools.getToolCallbacks()).forEach(toolCallback -> {
            System.out.println("----------------------");
            System.out.println(toolCallback.getToolDefinition());
            System.out.println("----------------------");
        });
        this.chatClient = builder
                .defaultSystem("""
                        Vous un assistant qui se charge de répondre aux question
                        de l'utilisateur en fonction du contexte fourni.
                        Si aucun contexte n'est fourni, répond avec JE NE SAIS PAS
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultToolCallbacks(tools)
                .build();
        System.out.println("AIAgent initialized successfully.");
    }

    @GetMapping("/chat")
    public String askAgent(String query) {
        return chatClient.prompt()
                .user(query)
                .call().content();
    }
}