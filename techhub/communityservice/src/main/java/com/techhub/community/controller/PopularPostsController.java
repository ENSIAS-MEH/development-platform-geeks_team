package com.techhub.community.controller;

import com.techhub.community.dto.PostResponse;
import com.techhub.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Popular Posts", description = "Cross-group popular posts (cached)")
public class PopularPostsController {

    private final PostService postService;

    @GetMapping("/popular")
    @Operation(summary = "Get popular posts across all groups (Redis cached)")
    public ResponseEntity<Page<PostResponse>> getPopularPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postService.getPopularPosts(page, size));
    }
}
