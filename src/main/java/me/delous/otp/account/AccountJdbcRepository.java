package me.delous.otp.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import me.delous.otp.account.dto.AccountView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class AccountJdbcRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Account> accountMapper = this::mapAccount;

    public AccountJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Account> byUsername(String username) {
        List<Account> rows = jdbc.query("""
                select account_id, username, password_digest, account_role, registered_at
                from accounts
                where username = ?
                """, accountMapper, username);
        return rows.stream().findFirst();
    }

    public Optional<Account> byId(long accountId) {
        List<Account> rows = jdbc.query("""
                select account_id, username, password_digest, account_role, registered_at
                from accounts
                where account_id = ?
                """, accountMapper, accountId);
        return rows.stream().findFirst();
    }

    public boolean hasAdminAccount() {
        Integer count = jdbc.queryForObject(
                "select count(*) from accounts where account_role = 'ADMIN'",
                Integer.class
        );
        return count != null && count > 0;
    }

    public Account insert(Account account) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement("""
                    insert into accounts(username, password_digest, account_role)
                    values (?, ?, ?)
                    """, new String[]{"account_id"});
            statement.setString(1, account.username());
            statement.setString(2, account.passwordDigest());
            statement.setString(3, account.accountRole().name());
            return statement;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        return byId(id).orElseThrow();
    }

    public List<AccountView> scanNonAdminAccounts() {
        return jdbc.query("""
                select account_id, username, account_role, registered_at
                from accounts
                where account_role <> 'ADMIN'
                order by account_id
                """, (rs, rowNum) -> new AccountView(
                rs.getLong("account_id"),
                rs.getString("username"),
                AccountRole.valueOf(rs.getString("account_role")),
                rs.getTimestamp("registered_at").toInstant()
        ));
    }

    public void eraseAccount(long accountId) {
        jdbc.update("delete from accounts where account_id = ? and account_role <> 'ADMIN'", accountId);
    }

    private Account mapAccount(ResultSet rs, int rowNum) throws SQLException {
        Timestamp registeredAt = rs.getTimestamp("registered_at");
        Instant instant = registeredAt.toInstant();
        return new Account(
                rs.getLong("account_id"),
                rs.getString("username"),
                rs.getString("password_digest"),
                AccountRole.valueOf(rs.getString("account_role")),
                instant
        );
    }
}
