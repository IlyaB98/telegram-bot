package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.entity.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    NotificationTask findByDispatchTime(LocalDateTime dateTime);
}
