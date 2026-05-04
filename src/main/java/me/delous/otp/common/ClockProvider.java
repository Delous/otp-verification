package me.delous.otp.common;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockProvider {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
