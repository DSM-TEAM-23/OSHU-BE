package com.dsm.oshu.inquiry.presentation;

import com.dsm.oshu.inquiry.presentation.dto.request.PostRequest;
import com.dsm.oshu.inquiry.presentation.dto.response.PostResponse;
import com.dsm.oshu.inquiry.service.PostCreateService;
import com.dsm.oshu.inquiry.service.PostDeleteService;
import com.dsm.oshu.inquiry.service.PostGetService;
import com.dsm.oshu.inquiry.service.PostUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PostController {

    private final PostCreateService postCreateService;
    private final PostGetService postGetService;
    private final PostUpdateService postUpdateService;
    private final PostDeleteService postDeleteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody PostRequest request){
        postCreateService.create(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPost(@PathVariable Long id){
        return postGetService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse updatePost(
            @PathVariable Long id,
            @RequestBody PostRequest request
    ) {
        return postUpdateService.execute(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id) {
        postDeleteService.execute(id);
    }
}
