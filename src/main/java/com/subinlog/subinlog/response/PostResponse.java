package com.subinlog.subinlog.response;

import com.subinlog.subinlog.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

//ResponseDTO
@Getter
public class PostResponse {
    private final Long id;
    private final String title;
    private final String content;

    //생성자 오버로딩을 통하여 post entity 받음.

    public PostResponse(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    @Builder
    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title.substring(0,Math.min(title.length(),10));
        this.content = content;
    }
}
