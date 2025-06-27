package com.crudApp.service.impl;

import com.crudApp.model.Label;
import com.crudApp.model.Post;
import com.crudApp.model.PostStatus;
import com.crudApp.repository.LabelRepository;
import com.crudApp.repository.PostRepository;
import com.crudApp.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PostServiceImplTest {

    private PostRepository postRepository;
    private LabelRepository labelRepository;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        labelRepository = mock(LabelRepository.class);
        postService = new PostServiceImpl(postRepository, labelRepository);
    }

    @Test
    void testCreatePost() {
        Label label = new Label(1L, "Tech");
        when(labelRepository.findById(1L)).thenReturn(label);

        Post savePost = new Post(1L, "Hello", null, null, List.of(label), PostStatus.ACTIVE);
        when(postRepository.save(any(Post.class))).thenReturn(savePost);

        Post result = postService.create("Hello", List.of(1L));

        assertEquals("Hello", result.getContent());
        assertEquals(PostStatus.ACTIVE, result.getStatus());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testUpdatePost() {
        Label newLabel = new Label(2L, "News");
        Post oldPost = new Post(1L, "Old", null, null, List.of(), PostStatus.ACTIVE);

        when(postRepository.findById(1L)).thenReturn(oldPost);
        when(labelRepository.findById(2L)).thenReturn(newLabel);
        when(postRepository.update(any(Post.class))).thenReturn(oldPost);

        Post result = postService.update(1L, "Updated", List.of(2L));

        assertEquals("Updated", result.getContent());
        assertEquals(1, result.getLabels().size());
        verify(postRepository).update(any(Post.class));
    }

    @Test
    void testUpdateStatus() {
        Post post = new Post(1L, "Some content", null, null, List.of(), PostStatus.ACTIVE);
        when(postRepository.findById(1L)).thenReturn(post);
        when(postRepository.update(any(Post.class))).thenReturn(post);

        Post updated = postService.updateStatus(1L, PostStatus.UNDER_REVIEW);
        assertEquals(PostStatus.UNDER_REVIEW, updated.getStatus());
    }
}
