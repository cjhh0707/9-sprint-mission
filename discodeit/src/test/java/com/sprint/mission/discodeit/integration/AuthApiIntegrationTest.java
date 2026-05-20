package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.config.TestSecurityConfigWithFormLogin;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfigWithFormLogin.class)
class AuthApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        userService.create(
            new UserCreateRequest("loginuser", "login@example.com", "Password1!"),
            Optional.empty()
        );

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "loginuser")
                .param("password", "Password1!"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.username", is("loginuser")))
            .andExpect(jsonPath("$.email", is("login@example.com")));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Failure_UserNotFound() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "nonexistentuser")
                .param("password", "Password1!"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Failure_InvalidCredentials() throws Exception {
        userService.create(
            new UserCreateRequest("loginuser2", "login2@example.com", "Password1!"),
            Optional.empty()
        );

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "loginuser2")
                .param("password", "WrongPassword1!"))
            .andExpect(status().isUnauthorized());
    }
}