package me.delous.otp.delivery;

import java.io.IOException;
import me.delous.otp.config.TelegramProperties;
import me.delous.otp.otp.DeliveryRoute;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TelegramGateway implements DeliveryGateway {
    private static final Logger log = LoggerFactory.getLogger(TelegramGateway.class);
    private final TelegramProperties properties;
    private final OkHttpClient client = new OkHttpClient();

    public TelegramGateway(TelegramProperties properties) {
        this.properties = properties;
    }

    @Override
    public DeliveryRoute route() {
        return DeliveryRoute.TELEGRAM;
    }

    @Override
    public void deliver(String target, String code) {
        RequestBody body = new FormBody.Builder()
                .add("chat_id", properties.chatId())
                .add("text", "%s, ваш код подтверждения: %s".formatted(target, code))
                .build();
        Request request = new Request.Builder()
                .url("https://api.telegram.org/bot%s/sendMessage".formatted(properties.botToken()))
                .post(body)
                .build();
        try (var response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalStateException("Telegram API вернул статус " + response.code());
            }
            log.info("delivery.telegram.sent target={}", target);
        } catch (IOException ex) {
            throw new IllegalStateException("Не удалось отправить сообщение Telegram", ex);
        }
    }
}
