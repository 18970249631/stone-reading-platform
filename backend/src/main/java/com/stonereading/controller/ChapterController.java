package com.stonereading.controller;

import com.stonereading.common.Result;
import com.stonereading.entity.Chapter;
import com.stonereading.entity.User;
import com.stonereading.service.ChapterService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapter")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/list/{bookId}")
    public Result<List<Chapter>> getChapterList(@PathVariable Long bookId) {
        List<Chapter> chapters = chapterService.getChapterList(bookId);
        return Result.success(chapters);
    }

    @GetMapping("/content/{id}")
    public Result<Chapter> getChapterContent(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Long userId = user != null ? user.getId() : null;
        Chapter chapter = chapterService.getChapterContent(id, userId);
        return Result.success(chapter);
    }

    @PostMapping("/create")
    public Result<Void> createChapter(@RequestBody Chapter chapter) {
        chapterService.createChapter(chapter);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> updateChapter(@RequestBody Chapter chapter) {
        chapterService.updateChapter(chapter);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return Result.success();
    }
}