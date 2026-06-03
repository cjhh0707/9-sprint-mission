package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String CLAIM_USERNAME = "username";
  private static final String CLAIM_ROLE = "role";

  private final MACSigner signer;
  private final MACVerifier verifier;
  private final long accessTokenExpirationMs;
  private final long refreshTokenExpirationMs;

  public JwtTokenProvider(
      @Value("${discodeit.jwt.secret}") String secret,
      @Value("${discodeit.jwt.access-token-expiration}") long accessTokenExpirationMs,
      @Value("${discodeit.jwt.refresh-token-expiration}") long refreshTokenExpirationMs
  ) {
    try {
      byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
      this.signer = new MACSigner(secretBytes);
      this.verifier = new MACVerifier(secretBytes);
    } catch (JOSEException e) {
      throw new IllegalArgumentException("JWT 시크릿 키 설정 오류 (최소 32자 필요): " + e.getMessage(), e);
    }
    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
  }

  public String generateAccessToken(UUID userId, String username, Role role) {
    try {
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(userId.toString())
          .claim(CLAIM_USERNAME, username)
          .claim(CLAIM_ROLE, role.name())
          .issueTime(new Date())
          .expirationTime(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException("액세스 토큰 생성 실패", e);
    }
  }

  public String generateRefreshToken(UUID userId) {
    try {
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(userId.toString())
          .issueTime(new Date())
          .expirationTime(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException("리프레시 토큰 생성 실패", e);
    }
  }

  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.verify(verifier)
          && new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());
    } catch (Exception e) {
      log.debug("토큰 유효성 검사 실패: {}", e.getMessage());
      return false;
    }
  }

  public UUID extractUserId(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return UUID.fromString(signedJWT.getJWTClaimsSet().getSubject());
    } catch (Exception e) {
      throw new RuntimeException("토큰에서 사용자 ID 추출 실패", e);
    }
  }

  public String extractUsername(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getStringClaim(CLAIM_USERNAME);
    } catch (Exception e) {
      throw new RuntimeException("토큰에서 사용자명 추출 실패", e);
    }
  }

  public Instant extractExpiration(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
    } catch (Exception e) {
      throw new RuntimeException("토큰에서 만료 시간 추출 실패", e);
    }
  }

  public String extractBearerToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      return authHeader.substring(BEARER_PREFIX.length());
    }
    return null;
  }
}