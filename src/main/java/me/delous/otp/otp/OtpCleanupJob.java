package me.delous.otp.otp;

import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OtpCleanupJob {
    private static final Logger log = LoggerFactory.getLogger(OtpCleanupJob.class);
    private final OtpJdbcRepository otpRepository;
    private final Clock clock;

    public OtpCleanupJob(OtpJdbcRepository otpRepository, Clock clock) {
        this.otpRepository = otpRepository;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${app.otp.cleanup-delay-ms:60000}")
    public void expireOldTickets() {
        int affected = otpRepository.expireOutdated(clock.instant());
        log.info("otp.cleanup.finished affectedRows={}", affected);
    }
}
