package com.crudApp.service;

import com.crudApp.model.Post;
import com.crudApp.model.PostStatus;

import java.util.List;

public interface PostService {
    Post create(String content, List<Long> labelIds);
    Post update(Long id, String newContent, List<Long> newLabelIds);
    void delete(Long id);
    Post getById(Long id);
    List<Post> getAll();
    Post updateStatus(Long id, PostStatus status);
}
