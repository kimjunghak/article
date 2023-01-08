package com.tradlinx.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradlinx.article.model.entity.Article;
import com.tradlinx.article.model.entity.Comments;
import com.tradlinx.article.model.entity.Member;
import com.tradlinx.article.model.front.ArticleFront;
import com.tradlinx.article.model.param.ArticleParam;
import com.tradlinx.article.model.param.CommentsParam;
import com.tradlinx.article.model.param.MemberParam;
import com.tradlinx.article.model.result.RestResult;
import com.tradlinx.article.service.article.ArticleService;
import com.tradlinx.article.service.article.CommentsService;
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
class CommentsTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final MemberApiService memberApiService;
    private final MemberService memberService;
    private final ArticleService articleService;
    private final CommentsService commentsService;

    String token;
    Article saved;
    Member member;

    @BeforeEach
    void setUp() {
        MemberParam memberParam = new MemberParam();
        memberParam.setUserid("member@test.com");
        memberParam.setPw("passw0rd");
        memberParam.setUsername("test_member");

        memberApiService.signup(memberParam);

        RestResult signin = memberApiService.signin(memberParam);
        token = signin.getData().toString();

        ArticleParam newArticle = new ArticleParam();
        newArticle.setArticleTitle("테스트 제목");
        newArticle.setArticleContents("테스트 내용");

        member = memberService.getMember(memberParam.getUserid());
        saved = articleService.upsertArticle(newArticle, member);
    }

    @Test
    @DisplayName("새_댓글_작성_성공")
    void create_comments_success() throws Exception {
        CommentsParam newComments = new CommentsParam();
        newComments.setArticleId(saved.getArticleId());
        newComments.setCommentsContents("댓글 테스트");

        String responseBody = mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(newComments))
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
    @DisplayName("새_댓글_작성_실패")
    void create_comments_fail() throws Exception {
        CommentsParam newComments = new CommentsParam();
        newComments.setArticleId(99L);
        newComments.setCommentsContents("댓글 테스트");

        Exception resolvedException = mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(newComments))
                )
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException();

        assertEquals("존재하지 않는 글입니다.", resolvedException.getMessage());
    }

    @Test
    @DisplayName("댓글_수정_성공")
    void modify_comments_success() throws Exception{
        CommentsParam newComments = new CommentsParam();
        newComments.setArticleId(saved.getArticleId());
        newComments.setCommentsContents("댓글 테스트");

        Comments comments = commentsService.upsertComments(newComments, saved, member);

        CommentsParam modifyComments = new CommentsParam();
        modifyComments.setArticleId(saved.getArticleId());
        modifyComments.setCommentsId(comments.getCommentsId());
        modifyComments.setCommentsContents("댓글 테스트 수정");

        String responseBody = mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(modifyComments))
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
    @DisplayName("댓글_수정_실패")
    void modify_comments_fail() throws Exception{
        CommentsParam newComments = new CommentsParam();
        newComments.setArticleId(saved.getArticleId());
        newComments.setCommentsContents("댓글 테스트");

        Comments comments = commentsService.upsertComments(newComments, saved, member);

        CommentsParam modifyComments = new CommentsParam();
        modifyComments.setArticleId(saved.getArticleId());
        modifyComments.setCommentsId(99L);
        modifyComments.setCommentsContents("댓글 테스트 수정");

        Exception resolvedException = mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(modifyComments))
                )
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException();

        assertEquals("존재하지 않는 댓글입니다.", resolvedException.getMessage());
    }

    @Test
    @DisplayName("댓글_삭제")
    void deleteComments() throws Exception{
        CommentsParam newComments = new CommentsParam();
        newComments.setArticleId(saved.getArticleId());
        newComments.setCommentsContents("댓글 테스트");

        Comments comments = commentsService.upsertComments(newComments, saved, member);

        String responseBody = mockMvc.perform(delete("/comments/" + comments.getCommentsId())
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

        assertTrue(restResult.isSuccess());
    }
}
