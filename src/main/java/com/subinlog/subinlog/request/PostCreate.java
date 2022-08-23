package com.subinlog.subinlog.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

//RequestDTO
@ToString
@Setter
@Getter
public class PostCreate {
    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public PostCreate(){
    }
    @NotBlank(message = "글 제목을 입력 해주세요.")
    public String title;
    @NotBlank(message = "글 내용을 입력 해주세요.")
    public String content;
}
