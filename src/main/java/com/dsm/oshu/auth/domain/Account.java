package com.dsm.oshu.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AccountRole role;

    protected Account() {
    }

    public Account(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
        this.role = AccountRole.OWNER;
    }

    @PrePersist
    void assignDefaultRole() {
        if (role == null) {
            role = AccountRole.OWNER;
        }
    }

    public Long getId() { return id; }
    public String getLoginId() { return loginId; }
    public String getPassword() { return password; }
    public AccountRole getRole() { return role == null ? AccountRole.OWNER : role; }
}
