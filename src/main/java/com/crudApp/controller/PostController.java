package com.crudApp.controller;

import com.crudApp.model.Post;
import com.crudApp.model.PostStatus;
import com.crudApp.service.PostService;

import java.util.List;

public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    public Post create(String content, List<Long> labelIds) {
        return postService.create(content, labelIds);
    }

    public Post update(Long id, String content, List<Long> labelIds) {
        return postService.update(id, content,labelIds);
    }

    public void delete(Long id) {
        postService.delete(id);
    }

    public Post getById(Long id) {
        return postService.getById(id);
    }

    public List<Post> getAll() {
        return postService.getAll();
    }

    public Post changeStatus(Long id, PostStatus status) {
        return postService.updateStatus(id, status);
    }
}
