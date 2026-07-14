package com.dsm.oshu.inquiry.domain.repository;

import com.dsm.oshu.inquiry.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
