package com.stonereading.service.impl;

import com.stonereading.entity.Book;
import com.stonereading.entity.Chapter;
import com.stonereading.mapper.BookMapper;
import com.stonereading.mapper.ChapterMapper;
import com.stonereading.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    private final BookMapper bookMapper;
    private final ChapterMapper chapterMapper;

    public SearchServiceImpl(BookMapper bookMapper, ChapterMapper chapterMapper) {
        this.bookMapper = bookMapper;
        this.chapterMapper = chapterMapper;
    }

    @Override
    public Map<String, Object> searchBooks(String keyword, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        List<Book> books = bookMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Book>()
                .like(Book::getTitle, keyword)
                .or()
                .like(Book::getAuthorName, keyword)
                .or()
                .like(Book::getDescription, keyword)
                .eq(Book::getStatus, 1)
        );

        result.put("total", books.size());
        result.put("books", books);
        return result;
    }

    @Override
    public Map<String, Object> searchChapters(Long bookId, String keyword) {
        Map<String, Object> result = new HashMap<>();
        
        List<Chapter> chapters = chapterMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getBookId, bookId)
                .like(Chapter::getTitle, keyword)
                .or()
                .like(Chapter::getContent, keyword)
        );

        result.put("total", chapters.size());
        result.put("chapters", chapters);
        return result;
    }

    @Override
    public void indexBook(Long bookId) {
        
    }

    @Override
    public void deleteIndex(Long bookId) {
        
    }
}
