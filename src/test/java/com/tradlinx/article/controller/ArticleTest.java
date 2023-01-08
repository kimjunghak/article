package com.tradlinx.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradlinx.article.config.JwtService;
import com.tradlinx.article.model.entity.Article;
import com.tradlinx.article.model.entity.Member;
import com.tradlinx.article.model.front.ArticleFront;
import com.tradlinx.article.model.front.MemberFront;
import com.tradlinx.article.model.param.ArticleParam;
import com.tradlinx.article.model.param.MemberParam;
import com.tradlinx.article.model.result.RestResult;
import com.tradlinx.article.service.article.ArticleApiService;
import com.tradlinx.article.service.article.ArticleService;
import com.tradlinx.article.service.member.MemberApiService;
import com.tradlinx.article.service.member.MemberService;
import lombok.RequiredArgsConstructor;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
@RequiredArgsConstructor
class ArticleTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final MemberApiService memberApiService;
    private final MemberService memberService;
    private final ArticleService articleService;

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
    @DisplayName("새_글_작성")
    void create_article() throws Exception {
        ArticleParam articleParam = new ArticleParam();
        articleParam.setArticleTitle("테스트 제목");
        articleParam.setArticleContents("테스트 내용");

        String responseBody = mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(articleParam))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        RestResult restResult = objectMapper.readValue(responseBody, RestResult.class);
        Object data = restResult.getData();
        System.out.println("data = " + data);

        assertEquals(1, data);
    }

    @Test
    @DisplayName("글_수정_성공")
    void modify_article_success() throws Exception{
        ArticleParam newArticle = new ArticleParam();
        newArticle.setArticleTitle("테스트 제목");
        newArticle.setArticleContents("테스트 내용");

        Member member = memberService.getMember("member@test.com");
        Article saved = articleService.upsertArticle(newArticle, member);

        ArticleParam modifyArticle = new ArticleParam();
        modifyArticle.setArticleId(saved.getArticleId());
        modifyArticle.setArticleTitle("테스트 제목 수정");
        modifyArticle.setArticleContents("테스트 내용 수정");

        String responseBody = mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(modifyArticle))
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
    @DisplayName("글_수정_실패")
    void modify_article_fail() throws Exception{
        ArticleParam newArticle = new ArticleParam();
        newArticle.setArticleTitle("테스트 제목");
        newArticle.setArticleContents("테스트 내용");

        Member member = memberService.getMember("member@test.com");
        Article saved = articleService.upsertArticle(newArticle, member);

        ArticleParam modifyArticle = new ArticleParam();
        modifyArticle.setArticleId(99L);
        modifyArticle.setArticleTitle("테스트 제목 수정");
        modifyArticle.setArticleContents("테스트 내용 수정");

        Exception resolvedException = mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(modifyArticle))
                )
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException();

        assertEquals("존재하지 않는 글입니다.", resolvedException.getMessage());
    }

    @Test
    @DisplayName("글_조회_성공")
    void get_article_success() throws Exception{
        ArticleParam articleParam = new ArticleParam();
        articleParam.setArticleTitle("테스트 제목");
        articleParam.setArticleContents("테스트 내용");

        Member member = memberService.getMember("member@test.com");
        articleService.upsertArticle(articleParam, member);


        String responseBody = mockMvc.perform(get("/article/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(articleParam))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        RestResult restResult = objectMapper.readValue(responseBody, RestResult.class);
        Object data = restResult.getData();
        System.out.println("data = " + data);
        ArticleFront articleFront = objectMapper.convertValue(data, ArticleFront.class);

        assertEquals("테스트 제목", articleFront.getArticleTitle());
    }

    @Test
    @DisplayName("글_조회_실패")
    void get_article_fail() throws Exception{
        ArticleParam newArticle = new ArticleParam();
        newArticle.setArticleTitle("테스트 제목");
        newArticle.setArticleContents("테스트 내용");

        Member member = memberService.getMember("member@test.com");
        Article saved = articleService.upsertArticle(newArticle, member);

        Exception resolvedException = mockMvc.perform(get("/article/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException();

        assertEquals("존재하지 않는 글입니다.", resolvedException.getMessage());
    }

    @Test
    @DisplayName("글_삭제")
    void deleteArticle() throws Exception{
        ArticleParam articleParam = new ArticleParam();
        articleParam.setArticleTitle("테스트 제목");
        articleParam.setArticleContents("테스트 내용");

        Member member = memberService.getMember("member@test.com");
        articleService.upsertArticle(articleParam, member);

        String responseBody = mockMvc.perform(delete("/article/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(articleParam))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        RestResult restResult = objectMapper.readValue(responseBody, RestResult.class);
        Object data = restResult.getData();
        System.out.println("data = " + data);

        assertEquals(1, data);
    }
}