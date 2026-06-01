package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final DiscodeitUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String bearerToken = jwtTokenProvider.extractBearerToken(request);

    if (bearerToken != null) {
      if (jwtTokenProvider.validateToken(bearerToken)
          && jwtRegistry.hasActiveJwtInformationByAccessToken(bearerToken)) {

        String username = jwtTokenProvider.extractUsername(bearerToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("JWT 인증 완료: username={}", username);
      } else {
        log.debug("유효하지 않은 JWT 토큰");
      }
    }

    filterChain.doFilter(request, response);
  }
}