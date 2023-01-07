package com.tradlinx.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradlinx.article.model.entity.Member;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
@RequiredArgsConstructor
class MemberControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입")
    void signup() throws Exception {
        Member member = Member.builder()
                .userid("member@test.com")
                .pw("passw0rd")
                .username("member")
                .build();

        String responseBody = mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(member))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(member.getUsername())))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        System.out.println("responseBody = " + responseBody);
    }
}