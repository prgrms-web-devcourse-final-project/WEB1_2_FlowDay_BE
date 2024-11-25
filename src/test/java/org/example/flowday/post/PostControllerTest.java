package org.example.flowday.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.post.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.post.dto.PostResponseDTO;
import org.example.flowday.domain.post.post.controller.PostController;
import org.example.flowday.domain.post.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    private PostResponseDTO postResponseDTO;
    private PostRequestDTO postRequestDTO;

    @BeforeEach
    void setUp() {
        postRequestDTO = new PostRequestDTO(
                2L,
                "서울",
                "테스트 제목",
                "테스트 내용",
                3L
        );

        postResponseDTO = PostResponseDTO.builder()
                .id(1L)
                .memberId(2L)
                .city("서울")
                .title("테스트 제목")
                .contents("테스트 내용")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .courseId(3L)
                .build();
    }

    @Test
    void 게시글_생성_테스트() throws Exception {
        Mockito.when(postService.createPost(any(PostRequestDTO.class))).thenReturn(postResponseDTO);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(postResponseDTO.getId()))
                .andExpect(jsonPath("$.memberId").value(postResponseDTO.getMemberId()))
                .andExpect(jsonPath("$.city").value(postResponseDTO.getCity()))
                .andExpect(jsonPath("$.title").value(postResponseDTO.getTitle()))
                .andExpect(jsonPath("$.contents").value(postResponseDTO.getContents()))
                .andExpect(jsonPath("$.courseId").value(postResponseDTO.getCourseId()));
    }

    @Test
    void 게시글_조회_성공_테스트() throws Exception {
        Mockito.when(postService.getPostById(1L)).thenReturn(Optional.of(postResponseDTO));

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postResponseDTO.getId()))
                .andExpect(jsonPath("$.memberId").value(postResponseDTO.getMemberId()))
                .andExpect(jsonPath("$.city").value(postResponseDTO.getCity()))
                .andExpect(jsonPath("$.title").value(postResponseDTO.getTitle()))
                .andExpect(jsonPath("$.contents").value(postResponseDTO.getContents()))
                .andExpect(jsonPath("$.courseId").value(postResponseDTO.getCourseId()));
    }

    @Test
    void 게시글_조회_실패_테스트() throws Exception {
        Mockito.when(postService.getPostById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 모든_게시글_조회_테스트() throws Exception {
        Mockito.when(postService.getAllPosts()).thenReturn(Arrays.asList(postResponseDTO));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(postResponseDTO.getId()))
                .andExpect(jsonPath("$[0].memberId").value(postResponseDTO.getMemberId()))
                .andExpect(jsonPath("$[0].city").value(postResponseDTO.getCity()))
                .andExpect(jsonPath("$[0].title").value(postResponseDTO.getTitle()))
                .andExpect(jsonPath("$[0].contents").value(postResponseDTO.getContents()))
                .andExpect(jsonPath("$[0].courseId").value(postResponseDTO.getCourseId()));
    }

    @Test
    void 게시글_수정_테스트() throws Exception {
        Mockito.when(postService.updatePost(anyLong(), any(PostRequestDTO.class))).thenReturn(postResponseDTO);

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postResponseDTO.getId()))
                .andExpect(jsonPath("$.memberId").value(postResponseDTO.getMemberId()))
                .andExpect(jsonPath("$.city").value(postResponseDTO.getCity()))
                .andExpect(jsonPath("$.title").value(postResponseDTO.getTitle()))
                .andExpect(jsonPath("$.contents").value(postResponseDTO.getContents()))
                .andExpect(jsonPath("$.courseId").value(postResponseDTO.getCourseId()));
    }

    @Test
    void 게시글_삭제_테스트() throws Exception {
        Mockito.doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());
    }
}
