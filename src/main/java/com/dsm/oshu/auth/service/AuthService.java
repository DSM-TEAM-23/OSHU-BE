package com.dsm.oshu.auth.service;

import com.dsm.oshu.auth.presentation.dto.LoginRequest;
import com.dsm.oshu.auth.presentation.dto.GoogleCodeExchangeRequest;
import com.dsm.oshu.auth.presentation.dto.MessageResponse;
import com.dsm.oshu.auth.presentation.dto.SignUpRequest;
import com.dsm.oshu.auth.presentation.dto.TokenResponse;
import com.dsm.oshu.auth.domain.Account;
import com.dsm.oshu.auth.domain.AccountRepository;
import com.dsm.oshu.auth.domain.GoogleLoginCode;
import com.dsm.oshu.auth.domain.GoogleLoginCodeRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AccountRepository accounts;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleLoginCodeRepository googleLoginCodes;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AccountRepository accounts, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                       GoogleLoginCodeRepository googleLoginCodes) {
        this.accounts = accounts;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.googleLoginCodes = googleLoginCodes;
    }

    @Transactional
    public MessageResponse signUp(SignUpRequest request) {
        if (accounts.existsByLoginId(request.loginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        accounts.save(new Account(request.loginId(), passwordEncoder.encode(request.password())));
        return new MessageResponse("회원가입이 완료되었습니다. 로그인 후 이용해주세요.");
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Account account = accounts.findByLoginId(request.loginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        account.ensureRole();
        return new TokenResponse(jwtTokenProvider.createAccessToken(account), "Bearer");
    }

    @Transactional
    public String createGoogleLoginCode(String googleSubject, String email) {
        Account account = accounts.findByGoogleSubject(googleSubject)
                .orElseGet(() -> accounts.save(Account.googleAccount(
                        googleSubject,
                        email,
                        passwordEncoder.encode(java.util.UUID.randomUUID().toString())
                )));
        googleLoginCodes.deleteByExpiresAtBefore(LocalDateTime.now());
        String code = createSecureCode();
        googleLoginCodes.save(new GoogleLoginCode(code, account.getLoginId(), LocalDateTime.now().plusMinutes(1)));
        log.info("Google OAuth login code issued");
        return code;
    }

    @Transactional
    public TokenResponse exchangeGoogleLoginCode(GoogleCodeExchangeRequest request) {
        GoogleLoginCode googleLoginCode = googleLoginCodes.findByCode(request.code())
                .orElseThrow(() -> {
                    log.warn("Google OAuth login code exchange failed: code not found");
                    return new IllegalArgumentException("유효하지 않은 로그인 코드입니다.");
                });
        if (googleLoginCodes.consume(request.code(), LocalDateTime.now()) != 1) {
            log.warn("Google OAuth login code exchange failed: code expired or already used");
            throw new IllegalArgumentException("만료되었거나 이미 사용한 로그인 코드입니다.");
        }
        Account account = accounts.findByLoginId(googleLoginCode.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("계정을 찾을 수 없습니다."));
        account.ensureRole();
        log.info("Google OAuth login code exchanged for an OSHU access token");
        return new TokenResponse(jwtTokenProvider.createAccessToken(account), "Bearer");
    }

    private String createSecureCode() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
