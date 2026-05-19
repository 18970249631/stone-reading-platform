package com.stonereading.controller;

import com.stonereading.common.Result;
import com.stonereading.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/books")
    public Result<Map<String, Object>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(searchService.searchBooks(keyword, page, size));
    }

    @GetMapping("/chapters/{bookId}")
    public Result<Map<String, Object>> searchChapters(
            @PathVariable Long bookId,
            @RequestParam String keyword) {
        return Result.success(searchService.searchChapters(bookId, keyword));
    }
}
