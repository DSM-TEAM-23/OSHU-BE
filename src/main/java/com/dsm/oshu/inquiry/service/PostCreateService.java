package com.dsm.oshu.inquiry.service;

import com.dsm.oshu.inquiry.domain.Post;
import com.dsm.oshu.inquiry.domain.repository.PostRepository;
import com.dsm.oshu.inquiry.presentation.dto.request.PostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCreateService {
    private final PostRepository postRepository;

    @Transactional
    public void create(PostRequest request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .name(request.getName())
                .number(request.getNumber())
                .build();

        postRepository.save(post);
    }
}
