package me.delous.otp.account;

import java.time.Instant;
import me.delous.otp.account.dto.AccountView;
import me.delous.otp.account.dto.SignupRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountRegistrationService {
    private final AccountJdbcRepository accounts;
    private final PasswordHasher passwordHasher;

    public AccountRegistrationService(AccountJdbcRepository accounts, PasswordHasher passwordHasher) {
        this.accounts = accounts;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public AccountView signup(SignupRequest request) {
        AccountRole role = request.requestedRole() == null ? AccountRole.USER : request.requestedRole();
        if (role == AccountRole.ADMIN && accounts.hasAdminAccount()) {
            throw new IllegalArgumentException("Администратор уже зарегистрирован");
        }
        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (request.rawPassword() == null || request.rawPassword().length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов");
        }

        Account saved = accounts.insert(new Account(
                0,
                request.username().trim(),
                passwordHasher.digest(request.rawPassword()),
                role,
                Instant.now()
        ));
        return new AccountView(saved.accountId(), saved.username(), saved.accountRole(), saved.registeredAt());
    }
}
