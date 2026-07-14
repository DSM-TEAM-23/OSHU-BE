package com.dsm.oshu.inquiry.service;

import com.dsm.oshu.inquiry.domain.Post;
import com.dsm.oshu.inquiry.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostDeleteService {

    private final PostRepository postRepository;

    @Transactional
    public void execute(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        postRepository.delete(post);
    }
}
