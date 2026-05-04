package me.delous.otp.otp;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OtpJdbcRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<OtpTicket> mapper = (rs, rowNum) -> new OtpTicket(
            rs.getLong("ticket_id"),
            rs.getLong("account_id"),
            rs.getString("business_key"),
            rs.getString("secret_code"),
            OtpState.valueOf(rs.getString("code_state")),
            rs.getTimestamp("issued_at").toInstant(),
            rs.getTimestamp("valid_until").toInstant(),
            rs.getTimestamp("consumed_at") == null ? null : rs.getTimestamp("consumed_at").toInstant()
    );

    public OtpJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public OtpTicket storeTicket(OtpTicket ticket) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement("""
                    insert into verification_codes(account_id, business_key, secret_code, code_state, valid_until)
                    values (?, ?, ?, ?, ?)
                    """, new String[]{"ticket_id"});
            statement.setLong(1, ticket.accountId());
            statement.setString(2, ticket.businessKey());
            statement.setString(3, ticket.secretCode());
            statement.setString(4, ticket.codeState().name());
            statement.setTimestamp(5, Timestamp.from(ticket.validUntil()));
            return statement;
        }, keyHolder);
        return byId(keyHolder.getKey().longValue()).orElseThrow();
    }

    public Optional<OtpTicket> byId(long ticketId) {
        List<OtpTicket> rows = jdbc.query("""
                select ticket_id, account_id, business_key, secret_code, code_state,
                       issued_at, valid_until, consumed_at
                from verification_codes
                where ticket_id = ?
                """, mapper, ticketId);
        return rows.stream().findFirst();
    }

    public Optional<OtpTicket> activeTicket(long accountId, String businessKey) {
        List<OtpTicket> rows = jdbc.query("""
                select ticket_id, account_id, business_key, secret_code, code_state,
                       issued_at, valid_until, consumed_at
                from verification_codes
                where account_id = ? and business_key = ? and code_state = 'ACTIVE'
                order by issued_at desc
                limit 1
                """, mapper, accountId, businessKey);
        return rows.stream().findFirst();
    }

    public void consume(long ticketId) {
        jdbc.update("""
                update verification_codes
                set code_state = 'USED', consumed_at = now()
                where ticket_id = ? and code_state = 'ACTIVE'
                """, ticketId);
    }

    public void expire(long ticketId) {
        jdbc.update("""
                update verification_codes
                set code_state = 'EXPIRED'
                where ticket_id = ? and code_state = 'ACTIVE'
                """, ticketId);
    }

    public int expireOutdated(Instant borderTime) {
        return jdbc.update("""
                update verification_codes
                set code_state = 'EXPIRED'
                where code_state = 'ACTIVE' and valid_until < ?
                """, Timestamp.from(borderTime));
    }
}
