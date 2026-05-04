package me.delous.otp.otp;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CodeFactory {
    private final SecureRandom secureRandom = new SecureRandom();

    public String numericSecret(int digitsCount) {
        if (digitsCount < 4 || digitsCount > 12) {
            throw new IllegalArgumentException("Длина OTP-кода должна быть от 4 до 12 цифр");
        }
        return secureRandom.ints(digitsCount, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }
}
