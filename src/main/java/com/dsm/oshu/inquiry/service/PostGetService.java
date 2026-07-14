package com.dsm.oshu.inquiry.service;

import com.dsm.oshu.inquiry.domain.Post;
import com.dsm.oshu.inquiry.domain.repository.PostRepository;
import com.dsm.oshu.inquiry.presentation.dto.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostGetService {

    private final PostRepository postRepository;

    public List<PostResponse> findAll(){
        return postRepository.findAll()
                .stream()
                .map(PostResponse::new)
                .toList();
    }

    public PostResponse findById(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        return new PostResponse(post);
    }
}
