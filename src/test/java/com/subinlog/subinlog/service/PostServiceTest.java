package com.subinlog.subinlog.service;

import com.subinlog.subinlog.domain.Post;
import com.subinlog.subinlog.repository.PostRepository;
import com.subinlog.subinlog.request.PostCreate;
import com.subinlog.subinlog.request.PostEdit;
import com.subinlog.subinlog.request.PostSearch;
import com.subinlog.subinlog.response.PostResponse;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@SpringBootTest
class PostServiceTest {
    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    public void clean() throws Exception{
        postRepository.deleteAll();
    }
    @Test
    @DisplayName("글 작성")
    public void test1() throws Exception{
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        //when
        postService.write(postCreate);
        //then
        assertEquals(1L, postRepository.count());
        assertEquals(postCreate.getTitle(), "제목입니다.");
        assertEquals(postCreate.getContent(), "내용입니다.");
    }

    @Test
    @DisplayName("글 1개 조회")
    public void test2() throws Exception{
        //given
        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        //when
        PostResponse postResponse = postService.get(requestPost.getId());

        //then
        assertNotNull(postResponse);
        assertEquals(1L, postRepository.count());
        assertEquals(postResponse.getTitle(), "foo");
        assertEquals(postResponse.getContent(), "bar");
    }

    @Test
    @DisplayName("글 1페이지 조회")
    public void test3() throws Exception{
        //given
        List<Post> requestPosts = IntStream.range(0, 20)
                        .mapToObj(i ->
                            Post.builder()
                                    .title("subinlog 제목 " + i)
                                    .content("반포자이 " + i)
                                    .build())
                        .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);
        PostSearch postSearch = PostSearch.builder()
                .build();

        //when
        List<PostResponse> posts = postService.getList(postSearch);

        //then
        assertEquals(10L, posts.size());
        assertEquals("subinlog 제목 19", posts.get(0).getTitle());
    }
    @Test
    @DisplayName("글 제목 수정")
    public void test4() throws Exception{
        //given
        Post post = Post.builder()
                .title("성공하게 해주세요.")
                .content("네카 부신다")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제발")
                .content("네카 부신다")
                .build();

        //when
        postService.edit(post.getId(), postEdit);
        //then
        Post changedPost = postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException
                ("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals(changedPost.getTitle(), "제발");
        assertEquals(changedPost.getContent(),"네카 부신다");
    }
    @Test
    @DisplayName("글 내용 수정")
    public void test5() throws Exception{
        //given
        Post post = Post.builder()
                .title("성공하게 해주세요.")
                .content("네카 부신다")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("성공하게 해주세요.")
                .content("네카 부실까?")
                .build();

        //when
        postService.edit(post.getId(), postEdit);
        //then
        Post changedPost = postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException
                ("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals(changedPost.getContent(),"네카 부실까?");
    }
}
















