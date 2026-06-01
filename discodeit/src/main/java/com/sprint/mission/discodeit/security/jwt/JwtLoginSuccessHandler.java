package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      ErrorResponse errorResponse = new ErrorResponse(
          new RuntimeException("Authentication failed: Invalid user details"),
          HttpServletResponse.SC_UNAUTHORIZED
      );
      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
      return;
    }

    UserDto userDto = userDetails.getUserDto();

    // 토큰 발급
    String accessToken = jwtTokenProvider.generateAccessToken(
        userDto.id(), userDto.username(), userDto.role());
    String refreshToken = jwtTokenProvider.generateRefreshToken(userDto.id());

    Instant accessTokenExpiry = jwtTokenProvider.extractExpiration(accessToken);
    Instant refreshTokenExpiry = jwtTokenProvider.extractExpiration(refreshToken);

    // 레지스트리에 등록 (동시 로그인 제한 포함)
    JwtInformation jwtInformation = new JwtInformation(
        userDto.id(), accessToken, refreshToken, accessTokenExpiry, refreshTokenExpiry
    );
    jwtRegistry.registerJwtInformation(jwtInformation);

    // 리프레시 토큰 → HttpOnly 쿠키에 저장
    Cookie refreshTokenCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    response.addCookie(refreshTokenCookie);

    // 액세스 토큰 → 응답 Body에 반환
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    JwtDto jwtDto = new JwtDto(accessToken);
    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
  }
}