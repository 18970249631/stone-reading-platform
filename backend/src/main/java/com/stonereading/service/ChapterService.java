package com.stonereading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonereading.entity.Chapter;

import java.util.List;

public interface ChapterService extends IService<Chapter> {

    List<Chapter> getChapterList(Long bookId);

    Chapter getChapterContent(Long chapterId, Long userId);

    void createChapter(Chapter chapter);

    void updateChapter(Chapter chapter);

    void deleteChapter(Long chapterId);
}