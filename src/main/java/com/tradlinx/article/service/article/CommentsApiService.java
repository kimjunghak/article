package com.tradlinx.article.service.article;

import com.tradlinx.article.config.JwtService;
import com.tradlinx.article.model.entity.Article;
import com.tradlinx.article.model.entity.Comments;
import com.tradlinx.article.model.entity.Member;
import com.tradlinx.article.model.param.CommentsParam;
import com.tradlinx.article.model.result.RestResult;
import com.tradlinx.article.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentsApiService {

    private final CommentsService commentsService;
    private final MemberService memberService;
    private final ArticleService articleService;
    private final JwtService jwtService;

    public RestResult upsertComments(CommentsParam param, HttpServletRequest request) {
        Member member = jwtService.getMember(request);
        Long articleId = param.getArticleId();
        Article article = articleService.getArticle(articleId);
        Comments comments = commentsService.upsertComments(param, article, member);
        articleService.save(comments.getArticle());

        // 원글 작성자 포인트 1 지급
        Member articleOwnMember = comments.getArticle().getMember();
        articleOwnMember.setPoint(articleOwnMember.getPoint() + 1);
        memberService.saveAll(List.of(member, articleOwnMember));

        return RestResult.success(comments.getCommentsId());
    }

    public RestResult deleteComments(Long commentsId, HttpServletRequest request) {
        Member member = jwtService.getMember(request);
        Comments comments = commentsService.getComments(commentsId);

        // 댓글 작성자 포인트 2 회수
        member.setPoint(member.getPoint() - 2);
        comments.setMember(member);

        // 원글 작성자 포인트 1 회수
        Member articleOwnMember = comments.getArticle().getMember();
        articleOwnMember.setPoint(articleOwnMember.getPoint() - 1);

        memberService.saveAll(new ArrayList<>(Arrays.asList(member, articleOwnMember)));

        commentsService.deleteComments(comments);
        return RestResult.success(commentsId);
    }
}
