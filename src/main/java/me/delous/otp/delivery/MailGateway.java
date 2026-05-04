package me.delous.otp.delivery;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import me.delous.otp.config.MailProperties;
import me.delous.otp.otp.DeliveryRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MailGateway implements DeliveryGateway {
    private static final Logger log = LoggerFactory.getLogger(MailGateway.class);
    private final MailProperties properties;
    private final Session session;

    public MailGateway(MailProperties properties) {
        this.properties = properties;
        Properties config = new Properties();
        config.put("mail.smtp.host", properties.smtp().host());
        config.put("mail.smtp.port", String.valueOf(properties.smtp().port()));
        config.put("mail.smtp.auth", String.valueOf(properties.smtp().auth()));
        config.put("mail.smtp.starttls.enable", String.valueOf(properties.smtp().starttlsEnable()));
        this.session = Session.getInstance(config, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.username(), properties.password());
            }
        });
    }

    @Override
    public DeliveryRoute route() {
        return DeliveryRoute.MAIL;
    }

    @Override
    public void deliver(String target, String code) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(properties.from()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(target));
            message.setSubject("Код подтверждения");
            message.setText("Ваш код подтверждения: " + code);
            Transport.send(message);
            log.info("delivery.mail.sent target={}", target);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Не удалось отправить письмо", ex);
        }
    }
}
