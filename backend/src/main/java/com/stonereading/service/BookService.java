package com.stonereading.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonereading.entity.Book;

import java.util.List;

public interface BookService extends IService<Book> {

    Page<Book> getBookList(Integer pageNum, Integer pageSize, String category, String keyword);

    Book getBookDetail(Long id);

    List<Book> getRecommendBooks(Long userId, Integer limit);

    List<Book> getFreeBooks(Integer limit);

    void addToBookshelf(Long userId, Long bookId);

    void removeFromBookshelf(Long userId, Long bookId);

    List<Book> getMyBookshelf(Long userId);
}