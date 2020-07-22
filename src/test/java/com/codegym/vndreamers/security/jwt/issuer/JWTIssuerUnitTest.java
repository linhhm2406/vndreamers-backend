package com.codegym.vndreamers.security.jwt.issuer;

import com.codegym.vndreamers.dtos.JWTResponse;
import com.codegym.vndreamers.models.User;
import com.codegym.vndreamers.services.auth.AuthService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class JWTIssuerUnitTest {
    private static final String API_AUTH_LOGIN = "/auth/login";
    private static final String API_AUTH_REGISTER = "/auth/register";
    public static final String VALID_FIRST_NAME = "valid_first_name";
    public static final String VALID_LAST_NAME = "valid_last_name";
    private static final String VALID_PASSWORD = "some_valid_password";
    private static final String VALID_TOKEN = "some_valid_token";
    private static final String VALID_EMAIL = "some_valid_email@example.com";
    private static final Timestamp VALID_BIRTH_DATE = Timestamp.valueOf(LocalDateTime.now());
    public static final String FAIL_PASSWORD = "some_fail_password";
    public static final String FAIL_USERNAME = "some_fail_username";
    private static final String FAIL_EMAIL = "some_fail_email";
    private static final String FAIL_BIRTH_DATE = "some_fail_date";

    private JSONObject payload;
    private static JWTResponse jwtResponse;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @BeforeAll
    static void mockData() {
        jwtResponse = new JWTResponse();
        User user = new User();
        user.setEmail(VALID_EMAIL);
        user.setFirstName(VALID_FIRST_NAME);
        user.setLastName(VALID_LAST_NAME);
        user.setPassword(VALID_PASSWORD);
        user.setBirthDate(VALID_BIRTH_DATE);
        user.setConfirmPassword(VALID_PASSWORD);
        user.setGender(1);
        user.setBirthDate(VALID_BIRTH_DATE);
        jwtResponse.setUser(user);
        jwtResponse.setAccessToken(VALID_TOKEN);
    }

    @BeforeEach
    void emptyPayload() {
        payload = new JSONObject();
    }

    @Test
    @DisplayName("Đăng nhập với trường hợp valid credential")
    void givenValidCredential_whenLoginPostRequest_thenOkAndReturnJWTResponse() throws Exception {
        when(authService.authenticate(any())).thenReturn(jwtResponse);

        payload.put("email", VALID_EMAIL);
        payload.put("password", VALID_PASSWORD);
        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andExpect(jsonPath("$.access_token", is(VALID_TOKEN)))
                .andExpect(jsonPath("$.user.username").exists())
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(jsonPath("$.user.email", is(VALID_EMAIL)))
                .andExpect(jsonPath("$.user.birthDay").exists());
    }

    @Test
    @DisplayName("Đăng nhập với body trống")
    void givenEmptyBody_whenLoginPostRequest_thenBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Đăng nhập với trường hợp để trống credential")
    void givenEmptyCredential_whenLoginPostRequest_thenBadRequest() throws Exception {
        payload.put("email", "");
        payload.put("password", "");
        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Đăng nhập với trường hợp để trống email")
    void givenEmptyUsername_whenLoginPostRequest_thenBadRequest() throws Exception {
        payload.put("email", "");
        payload.put("password", VALID_PASSWORD);
        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Đăng nhập với trường hợp để trống password")
    void givenEmptyPassword_whenLoginPostRequest_thenBadRequest() throws Exception {
        payload.put("email", VALID_EMAIL);
        payload.put("password", "");
        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Đăng nhập với trường hợp sai password")
    void givenWrongPassword_whenLoginPostRequest_thenUnauthorized() throws Exception {
        payload.put("email", VALID_EMAIL);
        payload.put("password", FAIL_PASSWORD);
        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Đăng nhập với trường hợp sai email")
    void givenWrongUsername_whenLoginPostRequest_thenUnauthorized() throws Exception {
        payload.put("email", FAIL_EMAIL);
        payload.put("password", FAIL_PASSWORD);
        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Đăng ký với thông tin hợp lệ")
    void givenValidBody_whenRegisterPostRequest_thenReturnOKAndJWTResponse() throws Exception {
        when(authService.register(any())).thenReturn(jwtResponse);

        payload.put("email", VALID_EMAIL);
        payload.put("first_name", "valid_first_name");
        payload.put("last_name", "valid_last_name");
        payload.put("password", VALID_PASSWORD);
        payload.put("confirm_password", VALID_PASSWORD);
        payload.put("birth_date", "20-04-1992");

        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_REGISTER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andExpect(jsonPath("$.access_token", is(VALID_TOKEN)))
                .andExpect(jsonPath("$.user.username").exists())
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(jsonPath("$.user.email", is(VALID_EMAIL)))
                .andExpect(jsonPath("$.user.birth_date").exists())
                .andExpect(jsonPath("$.user.phone").exists())
                .andExpect(jsonPath("$.user.status").exists())
                .andExpect(jsonPath("$.user.status", is(1)))
                .andExpect(jsonPath("$.user.avatar").exists())
                .andExpect(jsonPath("$.user.username").exists());
    }

    @Test
    @DisplayName("Đăng ký với body trống")
    void givenEmptyBody_whenRegisterPostRequest_thenBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_REGISTER))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Đăng ký với thông tin không hợp lệ")
    void givenInvalidEmail_whenRegisterPostRequest_thenBadRequest() throws Exception {
        payload.put("username", FAIL_USERNAME);
        payload.put("password", FAIL_PASSWORD);
        payload.put("email", FAIL_EMAIL);
        payload.put("birthDate", FAIL_BIRTH_DATE);
        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_REGISTER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Đăng ký với mật khẩu không trùng nhau")
    void givenPasswordNotMatch_whenRegisterPostRequest_thenBadRequest() throws Exception {
        payload.put("email", VALID_EMAIL);
        payload.put("first_name", "valid_first_name");
        payload.put("last_name", "valid_last_name");
        payload.put("password", VALID_PASSWORD);
        payload.put("confirm_password", FAIL_PASSWORD);
        payload.put("birth_date", "20-04-1992");

        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_REGISTER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Đăng ký với tài khoản trùng email")
    void givenDuplicateEmail_whenRegisterPostRequest_thenConflict() throws Exception {
        when(authService.register(any())).thenThrow(DataIntegrityViolationException.class);

        payload.put("email", VALID_EMAIL);
        payload.put("first_name", "valid_first_name");
        payload.put("last_name", "valid_last_name");
        payload.put("password", VALID_PASSWORD);
        payload.put("confirm_password", VALID_PASSWORD);
        payload.put("birth_date", "20-04-1992");

        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_REGISTER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict());
    }
}
