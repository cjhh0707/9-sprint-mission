package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    // 로그아웃은 인증 없이 호출될 수 있으므로 Authentication 대신 쿠키에서 리프레시 토큰을 사용
    if (request.getCookies() == null) {
      return;
    }

    Arrays.stream(request.getCookies())
        .filter(cookie -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
        .findFirst()
        .ifPresent(cookie -> {
          String refreshToken = cookie.getValue();

          // 리프레시 토큰으로 userId를 추출해 레지스트리에서 무효화
          try {
            java.util.UUID userId = jwtTokenProvider.extractUserId(refreshToken);
            jwtRegistry.invalidateJwtInformationByUserId(userId);
            log.debug("JWT 로그아웃 처리 완료: userId={}", userId);
          } catch (Exception e) {
            log.debug("로그아웃 중 토큰 파싱 실패 (무시): {}", e.getMessage());
          }

          // 리프레시 토큰 쿠키 삭제
          Cookie expiredCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, "");
          expiredCookie.setMaxAge(0);
          expiredCookie.setHttpOnly(true);
          expiredCookie.setPath("/");
          response.addCookie(expiredCookie);
        });
  }
}