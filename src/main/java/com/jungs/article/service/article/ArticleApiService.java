package com.jungs.article.service.article;

import com.jungs.article.model.entity.Article;
import com.jungs.article.model.entity.Comments;
import com.jungs.article.model.entity.Member;
import com.jungs.article.model.front.CommentsFront;
import com.jungs.article.model.front.MemberFront;
import com.jungs.article.model.mapper.CommentsMapper;
import com.jungs.article.model.param.ArticleParam;
import com.jungs.article.service.comment.CommentsService;
import com.jungs.article.service.member.MemberService;
import com.jungs.article.model.front.ArticleFront;
import com.jungs.article.model.mapper.ArticleMapper;
import com.jungs.article.model.result.RestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleApiService {

    private final ArticleService articleService;
    private final MemberService memberService;

    private final ArticleMapper articleMapper;
    private final CommentsMapper commentsMapper;

    @Transactional
    public RestResult createArticle(ArticleParam param, MemberFront memberFront) {
        Member member = memberService.getMember(memberFront.getUserid());

        Article article = articleService.createArticle(param, member);
        return RestResult.success(article.getArticleId());
    }

    public RestResult updateArticle(Long articleId, ArticleParam param, MemberFront authMember) {
        Article article = articleService.getArticle(articleId);
        if (isNotOwner(authMember.getMemberId(), article.getMember().getMemberId())) {
            return RestResult.fail("글 작성자만 수정할 수 있습니다.");
        }
        article.updateArticle(param);
        articleService.save(article);
        return RestResult.success(article.getArticleId());
    }

    @Transactional
    public RestResult getArticle(Long articleId) {
        Article article = articleService.getArticle(articleId);
        article.addViewCount();
        articleService.save(article);

        ArticleFront articleFront = getArticleFront(article);
        return RestResult.success(articleFront);
    }

    private ArticleFront getArticleFront(Article article) {
        ArticleFront articleFront = articleMapper.fromArticle(article);
        articleFront.setMemberName(article.getMember().getUsername());

        List<Comments> commentsList = article.getCommentsList();
        List<CommentsFront> commentsFronts = new ArrayList<>();
        for (Comments comments : commentsList) {
            CommentsFront commentsFront = commentsMapper.fromComments(comments);
            commentsFront.setMemberName(comments.getMember().getUsername());
            commentsFronts.add(commentsFront);
        }
        articleFront.setCommentsContentsList(commentsFronts);
        return articleFront;
    }

    @Transactional
    public RestResult deleteArticle(Long articleId, MemberFront authMember) {
        Article article = articleService.getArticle(articleId);

        Member owner = article.getMember();
        if (isNotOwner(authMember.getMemberId(), owner.getMemberId())) {
            return RestResult.fail("글 작성자만 삭제할 수 있습니다.");
        }

        //원글 작성 포인트 3 차감
        owner.addPoint(-3);

        // 댓글 포인트 모두 회수
        List<Comments> commentsList = article.getCommentsList();
        for (Comments comments : commentsList) {
            Member commentsMember = comments.getMember();
            owner.addPoint(-1); // 원글 댓글 작성 포인트 1 차감
            commentsMember.addPoint(-2); // 댓글 포인트 2 차감
        }

        articleService.deleteArticle(article);
        return RestResult.success(1);
    }

    private static boolean isNotOwner(Long authMemberId, Long ownerId) {
        return !authMemberId.equals(ownerId);
    }
}
