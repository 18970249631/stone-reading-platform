package com.stonereading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonereading.entity.Book;
import com.stonereading.entity.Bookshelf;
import com.stonereading.exception.BusinessException;
import com.stonereading.mapper.BookshelfMapper;
import com.stonereading.mapper.BookMapper;
import com.stonereading.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    private final BookshelfMapper bookshelfMapper;

    public BookServiceImpl(BookshelfMapper bookshelfMapper) {
        this.bookshelfMapper = bookshelfMapper;
    }

    @Override
    public Page<Book> getBookList(Integer pageNum, Integer pageSize, String category, String keyword) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(category)) {
            wrapper.eq(Book::getCategory, category);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                    .or().like(Book::getAuthorName, keyword)
                    .or().like(Book::getDescription, keyword));
        }

        wrapper.orderByDesc(Book::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    public Book getBookDetail(Long id) {
        Book book = getById(id);
        if (book != null) {
            book.setViewCount(book.getViewCount() + 1);
            updateById(book);
        }
        return book;
    }

    @Override
    public List<Book> getRecommendBooks(Long userId, Integer limit) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Book::getLikeCount, Book::getViewCount)
                .last("LIMIT " + limit);
        return list(wrapper);
    }

    @Override
    public List<Book> getFreeBooks(Integer limit) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsPaid, false)
                .orderByDesc(Book::getViewCount)
                .last("LIMIT " + limit);
        return list(wrapper);
    }

    @Override
    public void addToBookshelf(Long userId, Long bookId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserId, userId).eq(Bookshelf::getBookId, bookId);
        if (bookshelfMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("书籍已在书架中");
        }

        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setUserId(userId);
        bookshelf.setBookId(bookId);
        bookshelf.setReadProgress(0);
        bookshelfMapper.insert(bookshelf);
    }

    @Override
    public void removeFromBookshelf(Long userId, Long bookId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserId, userId).eq(Bookshelf::getBookId, bookId);
        bookshelfMapper.delete(wrapper);
    }

    @Override
    public List<Book> getMyBookshelf(Long userId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserId, userId)
                .orderByDesc(Bookshelf::getUpdatedAt);
        List<Bookshelf> shelfBooks = bookshelfMapper.selectList(wrapper);

        if (shelfBooks.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> bookIds = shelfBooks.stream().map(Bookshelf::getBookId).toList();
        return listByIds(bookIds);
    }
}