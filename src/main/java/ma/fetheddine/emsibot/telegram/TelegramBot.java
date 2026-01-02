package ma.fetheddine.emsibot.telegram;

import ma.fetheddine.emsibot.agents.AIAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Value("${telegram.api.key}")
    private String telegramBotToken;

    @Value("${telegram.bot.name}")
    private String botName;

    private AIAgent aiAgent;

    public TelegramBot(AIAgent aiAgent) {
        this.aiAgent = aiAgent;
        logger.info("TelegramBot bean created.");
    }

    @Bean
    public CommandLineRunner initTelegramBot() {
        return args -> {
            System.out.println("==================================================");
            System.out.println("Checking Telegram Bot Registration...");
            
            if (telegramBotToken == null || telegramBotToken.equals("${TELEGRAM_API_KEY}")) {
                System.err.println("ERROR: TELEGRAM API KEY IS NOT SET!");
                System.err.println("Please set the TELEGRAM_API_KEY environment variable.");
                System.out.println("==================================================");
                return;
            }

            try {
                TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
                api.registerBot(this);
                System.out.println("SUCCESS: Telegram Bot Registered Manually!");
            } catch (TelegramApiException e) {
                if (e.getMessage().contains("already registered")) {
                    System.out.println("INFO: Bot was already registered by Spring Starter. This is good.");
                } else {
                    System.err.println("ERROR: Failed to register bot manually: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.err.println("CRITICAL ERROR during bot registration: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("Bot Username: " + botName);
            System.out.println("Token: " + (telegramBotToken.length() > 5 ? telegramBotToken.substring(0, 5) + "..." : "INVALID"));
            System.out.println("==================================================");
        };
    }

    @Override
    public void onUpdateReceived(Update telegraRequest) {
        System.out.println("DEBUG: onUpdateReceived called!");
        try {
            if(!telegraRequest.hasMessage()) {
                System.out.println("DEBUG: No message in update");
                return;
            }
            if(!telegraRequest.getMessage().hasText()) {
                System.out.println("DEBUG: No text in message");
                return;
            }
            String messageText = telegraRequest.getMessage().getText();
            Long chatId = telegraRequest.getMessage().getChatId();
            
            System.out.println(">>> RECEIVED MESSAGE from " + chatId + ": " + messageText);
            logger.info("Received message from chatId {}: {}", chatId, messageText);

            sendTypingQuestion(chatId);
            String answer = aiAgent.askAgent(messageText);
            
            if (answer == null || answer.trim().isEmpty()) {
                answer = "Desole, je n'ai pas pu generer de reponse.";
                logger.warn("Agent returned empty response for message: {}", messageText);
            }

            System.out.println("<<< SENDING ANSWER: " + answer);
            logger.info("Agent response: {}", answer);
            sendTextMessage(chatId, answer);
        } catch (Exception e) {
            System.err.println("ERROR processing update: " + e.getMessage());
            e.printStackTrace();
            logger.error("Unexpected error in onUpdateReceived", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    private void sendTextMessage(long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        execute(sendMessage);
    }
    
    private void sendTypingQuestion(long chatId) throws TelegramApiException {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(String.valueOf(chatId));
        sendChatAction.setAction(ActionType.TYPING);
        execute(sendChatAction);
    }
}
