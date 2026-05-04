package me.delous.otp.delivery;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import me.delous.otp.otp.DeliveryRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalFileGateway implements DeliveryGateway {
    private static final Logger log = LoggerFactory.getLogger(LocalFileGateway.class);
    private final Path storage = Path.of("stored-otp.log");

    @Override
    public DeliveryRoute route() {
        return DeliveryRoute.FILE;
    }

    @Override
    public void deliver(String target, String code) {
        String line = "%s target=%s code=%s%n".formatted(Instant.now(), target, code);
        try {
            Files.writeString(
                    storage,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            log.info("delivery.file.saved target={}", target);
        } catch (IOException ex) {
            throw new IllegalStateException("Не удалось сохранить код в файл", ex);
        }
    }
}
