package com.dsm.oshu.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "google_login_codes")
public class GoogleLoginCode {
    @Id
    @Column(length = 64)
    private String code;

    @Column(nullable = false, length = 50)
    private String loginId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    protected GoogleLoginCode() {
    }

    public GoogleLoginCode(String code, String loginId, LocalDateTime expiresAt) {
        this.code = code;
        this.loginId = loginId;
        this.expiresAt = expiresAt;
    }

    public String getLoginId() {
        return loginId;
    }
}
