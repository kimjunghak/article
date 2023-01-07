package com.tradlinx.article.service.article;

import com.tradlinx.article.config.JwtService;
import com.tradlinx.article.model.entity.Article;
import com.tradlinx.article.model.entity.Comments;
import com.tradlinx.article.model.entity.Member;
import com.tradlinx.article.model.front.ArticleFront;
import com.tradlinx.article.model.mapper.ArticleMapper;
import com.tradlinx.article.model.param.ArticleParam;
import com.tradlinx.article.model.result.RestResult;
import com.tradlinx.article.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleApiService {

    private final ArticleService articleService;
    private final JwtService jwtService;
    private final MemberService memberService;

    private final ArticleMapper articleMapper;

    public RestResult upsertArticle(ArticleParam param, HttpServletRequest request) {
        Member member = jwtService.getMember(request);
        Article article = articleService.upsertArticle(param, member);
        memberService.save(article.getMember());
        return RestResult.success(article.getArticleId());
    }

    public RestResult getArticle(Long articleId, HttpServletRequest request) {
        jwtService.getMember(request);

        Article article = articleService.getArticle(articleId);
        int currentViewCount = article.getViewCount();
        article.setViewCount(currentViewCount + 1);
        articleService.save(article);
        ArticleFront articleFront = articleMapper.fromArticle(article);

        return RestResult.success(articleFront);
    }

    public RestResult deleteArticle(Long articleId, HttpServletRequest request) {
        jwtService.getMember(request);

        Article article = articleService.getArticle(articleId);

        // 글 작성 포인트 3 회수
        Member articleOwnMember = article.getMember();
        articleOwnMember.setPoint(articleOwnMember.getPoint() - 3);

        // 댓글 포인트 모두 회수
        List<Comments> commentsList = article.getCommentsList();
        ArrayList<Member> members = new ArrayList<>();
        for (Comments comments : commentsList) {
            Member commentsMember = comments.getMember();
            commentsMember.setPoint(commentsMember.getPoint() - 2); // 댓글 포인트 2 제거
            members.add(commentsMember);
        }
        articleOwnMember.setPoint(articleOwnMember.getPoint() - 1); // 원글 포인트 1 제거
        members.add(articleOwnMember);
        memberService.saveAll(members);

        articleService.deleteArticle(article);
        return RestResult.success(1);
    }
}
