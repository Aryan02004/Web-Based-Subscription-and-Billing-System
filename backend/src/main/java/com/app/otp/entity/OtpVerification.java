package com.app.otp.entity;

import java.time.LocalDateTime;

import com.app.auth.entity.User;
import com.app.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "otp_verifications")
@Setter
@Getter
@NoArgsConstructor
public class OtpVerification extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
    @Column(name = "otp_hash", nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private String purpose;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

}
