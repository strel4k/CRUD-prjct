package com.crudApp.service.impl;

import com.crudApp.model.Label;
import com.crudApp.model.Post;
import com.crudApp.model.PostStatus;
import com.crudApp.repository.LabelRepository;
import com.crudApp.repository.PostRepository;
import com.crudApp.service.PostService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final LabelRepository labelRepository;

    public PostServiceImpl(PostRepository postRepository, LabelRepository labelRepository) {
        this.postRepository = postRepository;
        this.labelRepository = labelRepository;
    }


    @Override
    public Post create(String content, List<Long> labelIds) {
        List<Label> labels = findLabels(labelIds);
        Post post = new Post(null, content, LocalDateTime.now(), LocalDateTime.now(), labels, PostStatus.ACTIVE);
        return postRepository.save(post);
    }

    @Override
    public Post update(Long id, String newContent, List<Long> newLabelIds) {
        Post post = postRepository.findById(id);
        if (post == null) throw new RuntimeException("Post not found");
        post.setContent(newContent);
        post.setUpdated(LocalDateTime.now());
        post.setLabels(findLabels(newLabelIds));
        return postRepository.update(post);
    }

    @Override
    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public Post getById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public Post updateStatus(Long id, PostStatus status) {
        Post post = postRepository.findById(id);
        if (post == null) throw new RuntimeException("Post not found");
        post.setStatus(status);
        post.setUpdated(LocalDateTime.now());
        return postRepository.update(post);
    }

    private List<Label> findLabels(List<Long> ids) {
        List<Label> labels = new ArrayList<>();
        for (Long id : ids) {
            Label label = labelRepository.findById(id);
            if (label != null) labels.add(label);
        }
        return labels;
    }
}
