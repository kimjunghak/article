package com.jungs.article.service.comment;

import com.jungs.article.model.entity.Article;
import com.jungs.article.model.entity.Comments;
import com.jungs.article.model.entity.Member;
import com.jungs.article.model.param.CommentsParam;
import com.jungs.article.repository.CommentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepository;


    public Comments createComments(CommentsParam param) {
        Comments comments = new Comments();
        comments.setCommentsContents(param.getCommentsContents());
        return save(comments);
    }

    public Comments getComments(Long commentsId) {
        return commentsRepository.findById(commentsId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 댓글입니다."));
    }

    public Comments save(Comments comments) {
        return commentsRepository.save(comments);
    }

    public void saveAll(List<Comments> commentsList) {
        commentsRepository.saveAll(commentsList);
    }

    public void deleteComments(Comments comments) {
        commentsRepository.delete(comments);
    }
}
