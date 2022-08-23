package com.subinlog.subinlog.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.subinlog.subinlog.domain.Post;
import com.subinlog.subinlog.request.PostSearch;
import lombok.RequiredArgsConstructor;
import com.subinlog.subinlog.domain.QPost;

import java.util.List;

import static com.subinlog.subinlog.domain.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostSearch postSearch){
        return jpaQueryFactory.selectFrom(post)
                .limit(postSearch.getSize()) //10개씩 가져오기.
                .offset(postSearch.getOffset())
                .orderBy(post.id.desc())
                .fetch();

    }
}
