package com.stonereading.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonereading.common.Result;
import com.stonereading.entity.Book;
import com.stonereading.entity.User;
import com.stonereading.service.BookService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/list")
    public Result<Page<Book>> getBookList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        Page<Book> page = bookService.getBookList(pageNum, pageSize, category, keyword);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result<Book> getBookDetail(@PathVariable Long id) {
        Book book = bookService.getBookDetail(id);
        return Result.success(book);
    }

    @GetMapping("/recommend")
    public Result<List<Book>> getRecommendBooks(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = user != null ? user.getId() : null;
        List<Book> books = bookService.getRecommendBooks(userId, limit);
        return Result.success(books);
    }

    @GetMapping("/free")
    public Result<List<Book>> getFreeBooks(@RequestParam(defaultValue = "20") Integer limit) {
        List<Book> books = bookService.getFreeBooks(limit);
        return Result.success(books);
    }

    @PostMapping("/shelf/add")
    public Result<Void> addToBookshelf(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Long> request) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        bookService.addToBookshelf(user.getId(), request.get("bookId"));
        return Result.success();
    }

    @DeleteMapping("/shelf/remove")
    public Result<Void> removeFromBookshelf(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Long> request) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        bookService.removeFromBookshelf(user.getId(), request.get("bookId"));
        return Result.success();
    }

    @GetMapping("/shelf")
    public Result<List<Book>> getMyBookshelf(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        List<Book> books = bookService.getMyBookshelf(user.getId());
        return Result.success(books);
    }
}