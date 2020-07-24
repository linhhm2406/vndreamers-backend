package com.codegym.vndreamers.services.comment;

import com.codegym.vndreamers.models.Comment;
import com.codegym.vndreamers.models.Post;
import com.codegym.vndreamers.services.GenericCRUDService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentService extends GenericCRUDService<Comment> {
    List<Comment> findAllExistByPost(Post post);
}
