package me.delous.otp.account;

import me.delous.otp.account.dto.LoginRequest;
import me.delous.otp.account.dto.TokenResponse;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {
    private final AccountJdbcRepository accounts;
    private final PasswordHasher passwordHasher;
    private final AuthTokenIssuer tokenIssuer;

    public LoginUseCase(AccountJdbcRepository accounts, PasswordHasher passwordHasher, AuthTokenIssuer tokenIssuer) {
        this.accounts = accounts;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
    }

    public TokenResponse issueToken(LoginRequest request) {
        Account account = accounts.byUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Неверный логин или пароль"));
        if (!passwordHasher.matches(request.rawPassword(), account.passwordDigest())) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }
        return tokenIssuer.issueToken(account);
    }
}
