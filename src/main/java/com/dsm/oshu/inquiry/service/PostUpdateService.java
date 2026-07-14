package com.dsm.oshu.inquiry.service;


import com.dsm.oshu.inquiry.domain.Post;
import com.dsm.oshu.inquiry.domain.repository.PostRepository;
import com.dsm.oshu.inquiry.presentation.dto.request.PostRequest;
import com.dsm.oshu.inquiry.presentation.dto.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUpdateService {

    private final PostRepository postRepository;

    @Transactional
    public PostResponse execute(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        post.update(request.getTitle(), request.getContent(), request.getName(), request.getNumber());

        return new  PostResponse(post);
    }
}
