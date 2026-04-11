package ru.battery.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.battery.email.dto.SendEmailDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailKafkaConsumer {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @KafkaListener(topics = "email", groupId = "email-group", containerFactory = "containerFactory")
    public void consumeEmail(SendEmailDto sendEmailDto) {
        String subject = "Подтверждение электронной почты";
        String textMessage = String.format(
                "Здравствуйте!\n\n" +
                        "Вы запрашивали код для подтверждения электронной почты.\n\n" +
                        "Ваш код: %s\n\n" +
                        "Пожалуйста, введите этот код в течение 5 минут, чтобы завершить подтверждение.\n\n" +
                        "Если вы не запрашивали этот код — просто проигнорируйте это письмо.\n\n" +
                        "С уважением,\n" +
                        "Команда поддержки \"ЫТ Студии\"",
                sendEmailDto.getVerifyCode()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(sendEmailDto.getEmail());
        message.setSubject(subject);
        message.setText(textMessage);
        log.info("Sending email to {}; with message {}; from {}", sendEmailDto.getEmail(), textMessage, from);
        mailSender.send(message);
    }
}
