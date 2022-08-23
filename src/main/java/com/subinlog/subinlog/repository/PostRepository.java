package com.subinlog.subinlog.repository;

import com.subinlog.subinlog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    //<Post 엔티티, ID>
}
