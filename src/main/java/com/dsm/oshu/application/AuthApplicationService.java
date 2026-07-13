package com.dsm.oshu.application;

import com.dsm.oshu.application.dto.OshuDtos.LoginRequest;
import com.dsm.oshu.application.dto.OshuDtos.MessageResponse;
import com.dsm.oshu.application.dto.OshuDtos.SignUpRequest;
import com.dsm.oshu.application.dto.OshuDtos.TokenResponse;
import com.dsm.oshu.domain.account.Account;
import com.dsm.oshu.domain.account.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthApplicationService {
    private final AccountRepository accounts;
    private final PasswordEncoder passwordEncoder;
    private final String ownerToken;

    public AuthApplicationService(AccountRepository accounts, PasswordEncoder passwordEncoder,
                                  @Value("${oshu.owner-token}") String ownerToken) {
        this.accounts = accounts;
        this.passwordEncoder = passwordEncoder;
        this.ownerToken = ownerToken;
    }

    @Transactional
    public MessageResponse signUp(SignUpRequest request) {
        if (accounts.existsByLoginId(request.loginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        accounts.save(new Account(request.loginId(), passwordEncoder.encode(request.password())));
        return new MessageResponse("회원가입이 완료되었습니다. 로그인 후 이용해주세요.");
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Account account = accounts.findByLoginId(request.loginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        // Development token: replace this static token with signed JWT issuance before deployment.
        return new TokenResponse(ownerToken, "Bearer");
    }
}
