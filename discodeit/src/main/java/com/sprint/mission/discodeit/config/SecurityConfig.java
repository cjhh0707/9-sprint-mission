package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.security.CustomAuthenticationEntryPoint;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;

  @Bean
  @Order(2)
  public SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler))
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
        .rememberMe(rm -> rm
            .key("discodeit-remember-me")
            .tokenValiditySeconds(86400)  // 1일
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().permitAll()
        )
        .sessionManagement(management -> management
            .sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry)
            )
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
    ;
    return http.build();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(
        "ROLE_ADMIN > ROLE_CHANNEL_MANAGER\nROLE_CHANNEL_MANAGER > ROLE_USER"
    );
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler =
        new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }
}