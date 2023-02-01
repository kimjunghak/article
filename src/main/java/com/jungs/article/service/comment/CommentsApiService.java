package com.jungs.article.service.comment;

import com.jungs.article.model.entity.Article;
import com.jungs.article.model.entity.Comments;
import com.jungs.article.model.entity.Member;
import com.jungs.article.model.front.MemberFront;
import com.jungs.article.model.param.CommentsParam;
import com.jungs.article.model.result.RestResult;
import com.jungs.article.service.article.ArticleService;
import com.jungs.article.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentsApiService {

    private final CommentsService commentsService;
    private final MemberService memberService;
    private final ArticleService articleService;

    @Transactional
    public RestResult createComments(CommentsParam param, MemberFront memberFront) {
        Long articleId = param.getArticleId();
        Article article = articleService.getArticle(articleId);
        Member member = memberService.getMember(memberFront.getUserid());

        Comments comments = commentsService.createComments(param);
        comments.setArticle(article);

        // 댓글 작성자 포인트 2 지급
        member.addPoint(2);
        comments.setMember(member);

        // 원글 작성자 포인트 1 지급
        Member articleOwnMember = comments.getArticle().getMember();
        articleOwnMember.addPoint(1);
        memberService.save(articleOwnMember);

        return RestResult.success(comments.getCommentsId());
    }

    public RestResult updateComments(Long commentsId, CommentsParam param, MemberFront authMember) {
        Comments comments = commentsService.getComments(commentsId);
        if (isNotOwner(authMember.getMemberId(), comments.getMember().getMemberId())) {
            return RestResult.fail("댓글 작성자만 수정할 수 있습니다.");
        }

        comments.setCommentsContents(param.getCommentsContents());
        commentsService.save(comments);

        return RestResult.success(comments.getCommentsId());
    }

    @Transactional
    public RestResult deleteComments(Long commentsId, MemberFront authMember) {
        Comments comments = commentsService.getComments(commentsId);

        if (isNotOwner(authMember.getMemberId(), comments.getMember().getMemberId())) {
            return RestResult.fail("댓글 작성자만 삭제할 수 있습니다.");
        }

        // 원글 작성자 포인트 1 회수
        Member articleOwnMember = comments.getArticle().getMember();
        articleOwnMember.addPoint(-1);
        memberService.save(articleOwnMember);

        // 댓글 작성자 포인트 2 회수
        Member member = comments.getMember();
        member.addPoint(-2);

        commentsService.deleteComments(comments);
        return RestResult.success(commentsId);
    }

    private static boolean isNotOwner(Long authMemberId, Long ownerMemberId) {
        return !authMemberId.equals(ownerMemberId);
    }
}
