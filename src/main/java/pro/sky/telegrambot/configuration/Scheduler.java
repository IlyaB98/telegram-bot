package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableScheduling
public class Scheduler {
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository repository;

    public Scheduler(TelegramBot telegramBot, NotificationTaskRepository repository) {
        this.telegramBot = telegramBot;
        this.repository = repository;
    }

    //Проверяет наличие задач в БД для отправки
    @Scheduled(cron = "0 0/1 * * * *")
    public void schedulerStart() {
        NotificationTask task = repository.findByDispatchTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        if (task != null) {
            String message = "У Вас запланирована задача: \n";
            SendMessage reminder = new SendMessage(task.getChatId(), message + task.getMassage());
            telegramBot.execute(reminder);
        }
    }
}

