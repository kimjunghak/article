package com.tradlinx.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradlinx.article.model.front.MemberFront;
import com.tradlinx.article.model.param.MemberParam;
import com.tradlinx.article.model.result.RestResult;
import com.tradlinx.article.service.member.MemberApiService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
@RequiredArgsConstructor
public class MemberTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final MemberApiService memberApiService;

    String token;

    @BeforeEach
    void setUp() {
        MemberParam memberParam = new MemberParam();
        memberParam.setUserid("member@test.com");
        memberParam.setPw("passw0rd");
        memberParam.setUsername("test_member");

        memberApiService.signup(memberParam);

        RestResult signin = memberApiService.signin(memberParam);
        token = signin.getData().toString();
    }

    @Test
    @DisplayName("프로필_조회")
    void getProfile() throws Exception {
        String responseBody = mockMvc.perform(get("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        RestResult restResult = objectMapper.readValue(responseBody, RestResult.class);
        Object data = restResult.getData();
        System.out.println("data = " + data);
        MemberFront memberFront = objectMapper.convertValue(data, MemberFront.class);

        assertEquals("test_member", memberFront.getUsername());
    }

    @Test
    @DisplayName("포인트_조회")
    void getPoint() throws Exception {
        String responseBody = mockMvc.perform(get("/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        RestResult restResult = objectMapper.readValue(responseBody, RestResult.class);
        Object data = restResult.getData();
        System.out.println("data = " + data);

        assertEquals(0, data);
    }
}
