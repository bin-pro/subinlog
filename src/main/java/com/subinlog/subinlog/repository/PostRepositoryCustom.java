package com.subinlog.subinlog.repository;

import com.subinlog.subinlog.domain.Post;
import com.subinlog.subinlog.request.PostSearch;

import java.awt.print.Pageable;
import java.util.List;

public interface PostRepositoryCustom {
    List<Post> getList(PostSearch postSearch);
}
