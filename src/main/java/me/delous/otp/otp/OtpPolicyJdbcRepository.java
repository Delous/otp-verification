package me.delous.otp.otp;

import me.delous.otp.admin.dto.OtpPolicyView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OtpPolicyJdbcRepository {
    private final JdbcTemplate jdbc;

    public OtpPolicyJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public OtpPolicyView currentPolicy() {
        return jdbc.queryForObject("""
                select digits_count, ttl_seconds
                from otp_policy
                where policy_id = 1
                """, (rs, rowNum) -> new OtpPolicyView(
                rs.getInt("digits_count"),
                rs.getInt("ttl_seconds")
        ));
    }

    public OtpPolicyView refreshPolicy(int digitsCount, int ttlSeconds) {
        jdbc.update("""
                insert into otp_policy(policy_id, digits_count, ttl_seconds)
                values (1, ?, ?)
                on conflict (policy_id)
                do update set digits_count = excluded.digits_count,
                              ttl_seconds = excluded.ttl_seconds
                """, digitsCount, ttlSeconds);
        return currentPolicy();
    }
}
