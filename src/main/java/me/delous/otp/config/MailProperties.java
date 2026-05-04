package me.delous.otp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail")
public record MailProperties(
        String username,
        String password,
        String from,
        Smtp smtp
) {
    public record Smtp(String host, int port, boolean auth, boolean starttlsEnable) {
    }
}
