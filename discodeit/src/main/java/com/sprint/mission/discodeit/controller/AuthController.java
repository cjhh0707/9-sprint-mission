package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PostMapping("refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
    log.info("토큰 재발급 요청");

    if (request.getCookies() == null) {
      return unauthorized("리프레시 토큰이 없습니다.");
    }

    Optional<String> refreshTokenOpt = Arrays.stream(request.getCookies())
        .filter(c -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
        .map(Cookie::getValue)
        .findFirst();

    if (refreshTokenOpt.isEmpty()) {
      return unauthorized("리프레시 토큰이 없습니다.");
    }

    String refreshToken = refreshTokenOpt.get();

    if (!jwtTokenProvider.validateToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      return unauthorized("유효하지 않은 리프레시 토큰입니다.");
    }

    UUID userId = jwtTokenProvider.extractUserId(refreshToken);
    UserDto userDto = userService.find(userId);

    // 새 토큰 생성 (Rotation)
    String newAccessToken = jwtTokenProvider.generateAccessToken(
        userDto.id(), userDto.username(), userDto.role());
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDto.id());

    JwtInformation newJwtInformation = new JwtInformation(
        userId, newAccessToken, newRefreshToken,
        jwtTokenProvider.extractExpiration(newAccessToken),
        jwtTokenProvider.extractExpiration(newRefreshToken)
    );

    jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

    // 새 리프레시 토큰을 쿠키에 업데이트
    Cookie cookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, newRefreshToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    response.addCookie(cookie);

    log.info("토큰 재발급 완료: userId={}", userId);
    return ResponseEntity.ok(new JwtDto(newAccessToken));
  }

  @PutMapping("role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    log.info("권한 수정 요청");
    UserDto userDto = authService.updateRole(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  private ResponseEntity<ErrorResponse> unauthorized(String message) {
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(), "UNAUTHORIZED", message,
        new HashMap<>(), "UnauthorizedException", HttpServletResponse.SC_UNAUTHORIZED
    );
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }
}