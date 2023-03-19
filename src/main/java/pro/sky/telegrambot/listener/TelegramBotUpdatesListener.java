package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final NotificationTaskRepository repository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository repository) {
        this.telegramBot = telegramBot;
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            SendMessage greeting = new SendMessage(update.message().chat().id(),
                    "Привет, " + update.message().from().firstName() + "!");

            String infoMsg = "Я умею запоминать за Вас ваши дела \uD83D\uDE0A. \n" +
                    "Просто отправь мне сообщение в таком формате -\n" +
                    "21.01.1999 21:00 Текст вашей задачи\n\n" +
                    "И сможешь выбросить ее из головы, " +
                    "я с точностью до минуты пришлю вам уведомление о текущей задаче \uD83D\uDE09.";
            SendMessage info = new SendMessage(update.message().chat().id(), infoMsg);
            String message = update.message().text();

            logger.info("Processing update: {}", update);

            if (message != null && update.message().text().equals("/start")) {
                telegramBot.execute(greeting);
                telegramBot.execute(info);
            } else {
                Pattern regexp = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
                assert message != null;
                Matcher matcher = regexp.matcher(message);

                NotificationTask task = new NotificationTask();

                //Создание задачи и сохранение в БД
                if (matcher.matches()) {
                    task.setChatId(update.message().chat().id());
                    task.setDispatchTime(Timestamp.valueOf(LocalDateTime.parse(matcher.group(1),
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).toLocalDateTime());
                    task.setMassage(matcher.group(3));
                    repository.save(task);

                    SendMessage notification = new SendMessage(update.message().chat().id(),
                            "Я сохранил Вашу задачу, забудь ее и займись своими делами, " +
                                    "я напомню \uD83D\uDE09.");
                    telegramBot.execute(notification);
                }

            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
