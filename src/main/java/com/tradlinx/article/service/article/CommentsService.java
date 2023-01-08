package com.tradlinx.article.service.article;

import com.tradlinx.article.model.entity.Article;
import com.tradlinx.article.model.entity.Comments;
import com.tradlinx.article.model.entity.Member;
import com.tradlinx.article.model.param.CommentsParam;
import com.tradlinx.article.repository.CommentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepository;


    public Comments upsertComments(CommentsParam param, Article article, Member member) {
        Long commentsId = param.getCommentsId();
        Comments comments;
        if (commentsId == null) {
            comments = new Comments();
            comments.setArticle(article);
        } else {
            comments = getComments(commentsId);
        }
        comments.setCommentsContents(param.getCommentsContents());

        // 댓글 작성자 포인트 2 지급
        member.setPoint(member.getPoint() + 2);
        comments.setMember(member);

        return save(comments);
    }

    public Comments getComments(Long commentsId) {
        return commentsRepository.findById(commentsId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 댓글입니다."));
    }

    public Comments save(Comments comments) {
        return commentsRepository.save(comments);
    }

    public void deleteComments(Comments comments) {
        commentsRepository.delete(comments);
    }
}
