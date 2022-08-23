package com.subinlog.subinlog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subinlog.subinlog.domain.Post;
import com.subinlog.subinlog.repository.PostRepository;
import com.subinlog.subinlog.request.PostCreate;
import com.subinlog.subinlog.request.PostEdit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest //간단한거, 서비스 레포지토리 등 애플리케이션에는 못씀
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 요청시 Hello World 출력한다.")
    void test() throws Exception{
        //글 제목
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);

        System.out.println(json);
        //글 내용
        //mockmvc란 controller를 호출해주는 테스트 도구이며 request, response에 관련되어 사용 가능하다. HTTP메세지 제어 가능
        //기본적으로 데이터를 application/JSON형태로 보냄. Header의 content type에 데이터의 형식을 명시한다.
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\": \"제목입니다.\", \"content\": \"내용입니다.\"}")
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 title 값은 필수다.")
    void test2() throws Exception{
        //given
        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        //when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )//then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                 .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                 .andExpect(jsonPath("$.validation.title").value("글 제목을 입력 해주세요."))
                 .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 DB값이 저장된다.")
    void test3() throws Exception{
        //given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        //when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\": \"제목입니다.\", \"content\": \"내용입니다.\"}")
                )
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L,postRepository.count());

        Post post = postRepository.findAll().get(0);

        //then
        assertEquals("제목입니다.", post.getTitle());

    }

    @Test
    @DisplayName("글 1개 조회")
    public void test4() throws Exception{
        //given
        Post post = Post.builder()
                .title("123456789012345")
                .content("bar")
                .build();
        postRepository.save(post);
        //expected(when + then)
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("1234567890"))
                        .andExpect(jsonPath("$.content").value("bar"))
                        .andDo(print());
    }
    @Test
    @DisplayName("글 여러개 조회")
    public void test5() throws Exception{
        //given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj(i ->
                        Post.builder()
                                .title("subinlog 제목 " + i)
                                .content("반포자이 " + i)
                                .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        //expected(when + then)
        mockMvc.perform(get("/posts?page=1&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.length()"), is(10)))
                .andExpect(jsonPath("$[0].content").value("반포자이 19"))
                .andExpect(jsonPath("$[0].title").value("subinlog 제목 19"))
                .andDo(print());
    }

    @Test
    @DisplayName("페이지 0 가져올 시 페이지 1 가져온다.")
    public void test6() throws Exception{
        //given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj(i ->
                        Post.builder()
                                .title("subinlog 제목 " + i)
                                .content("반포자이 " + i)
                                .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        //expected(when + then)
        mockMvc.perform(get("/posts?page=0&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.length()"), is(10)))
                .andExpect(jsonPath("$[0].content").value("반포자이 19"))
                .andExpect(jsonPath("$[0].title").value("subinlog 제목 19"))
                .andDo(print());
    }
    @Test
    @DisplayName("글 제목 수정")
    void test7() throws Exception {
        // given
        Post post = Post.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("호돌걸")
                .content("반포자이")
                .build();

        // expected
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}





















