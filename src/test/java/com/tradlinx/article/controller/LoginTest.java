package com.tradlinx.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradlinx.article.model.param.MemberParam;
import com.tradlinx.article.model.result.RestResult;
import com.tradlinx.article.service.member.MemberApiService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
@RequiredArgsConstructor
class LoginTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final MemberApiService memberApiService;

    @Test
    @Disabled
    @DisplayName("회원가입")
    void signup() throws Exception {
        MemberParam memberParam = new MemberParam();
        memberParam.setUserid("member@test.com");
        memberParam.setPw("passw0rd");
        memberParam.setUsername("test_member");

        String responseBody = mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(memberParam))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        RestResult restResult = objectMapper.readValue(responseBody, RestResult.class);
        Object data = restResult.getData();
        System.out.println("data = " + data);

        assertEquals(memberParam.getUsername(), data.toString());
    }


    @Test
    @DisplayName("로그인_성공")
    void signin_success() throws Exception {
        MemberParam signup = new MemberParam();
        signup.setUserid("member@test.com");
        signup.setPw("passw0rd");
        signup.setUsername("test_member");

        memberApiService.signup(signup);


        MemberParam signin = new MemberParam();
        signin.setUserid("member@test.com");
        signin.setPw("passw0rd");

        String responseBody = mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(signin))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        RestResult restResult = objectMapper.readValue(responseBody, RestResult.class);
        Object data = restResult.getData();
        System.out.println("data = " + data);

        assertTrue(restResult.isSuccess());
    }

    @Test
    @DisplayName("로그인_실패_존재하지_않는_사용자")
    void signin_fail_notfound() throws Exception {
        MemberParam signin = new MemberParam();
        signin.setUserid("member@test.com");
        signin.setPw("passw0rd");

        Exception resolvedException = mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(signin))
                )
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException();

        assertEquals("존재하지 않는 이용자입니다.", resolvedException.getMessage());
    }

    @Test
    @DisplayName("로그인_실패_잘못된_암호")
    void signin_fail_password() throws Exception {
        MemberParam signup = new MemberParam();
        signup.setUserid("member@test.com");
        signup.setPw("passw0rd");
        signup.setUsername("test_member");

        memberApiService.signup(signup);

        MemberParam signin = new MemberParam();
        signin.setUserid("member@test.com");
        signin.setPw("passw0rd1");

        Exception resolvedException = mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(signin))
                )
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResolvedException();

        assertEquals("암호가 일치하지 않습니다.", resolvedException.getMessage());
    }
}