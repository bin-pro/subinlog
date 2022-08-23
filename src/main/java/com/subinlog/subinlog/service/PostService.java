package com.subinlog.subinlog.service;

import com.subinlog.subinlog.domain.Post;
import com.subinlog.subinlog.domain.PostEditor;
import com.subinlog.subinlog.repository.PostRepository;
import com.subinlog.subinlog.request.PostCreate;
import com.subinlog.subinlog.request.PostEdit;
import com.subinlog.subinlog.request.PostSearch;
import com.subinlog.subinlog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    @Autowired
    private final PostRepository postRepository;
    public void write(PostCreate postCreate){
        // postCreate -> Entity
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();
        postRepository.save(post);
    }

    public PostResponse get(Long id){
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 글입니다."));
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
    //다중 조회의 단점
    //글이 너무 많은 경우 -> 비용이 너무 많이 든다.
    //그래서 페이징 처리를 하는 것이 좋다.
    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }
    @Transactional
    public void edit(Long id, PostEdit postEdit){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        PostEditor.PostEditorBuilder postEditorBuilder = post.toEditor();
        PostEditor postEditor = postEditorBuilder.title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();
        post.edit(postEditor);
    }
}


















