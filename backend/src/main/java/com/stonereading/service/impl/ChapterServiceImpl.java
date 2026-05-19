package com.stonereading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonereading.entity.Book;
import com.stonereading.entity.Chapter;
import com.stonereading.entity.User;
import com.stonereading.exception.BusinessException;
import com.stonereading.mapper.BookMapper;
import com.stonereading.mapper.ChapterMapper;
import com.stonereading.service.ChapterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    private final BookMapper bookMapper;

    public ChapterServiceImpl(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Override
    public List<Chapter> getChapterList(Long bookId) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getBookId, bookId)
                .orderByAsc(Chapter::getChapterNum);
        return list(wrapper);
    }

    @Override
    public Chapter getChapterContent(Long chapterId, Long userId) {
        Chapter chapter = getById(chapterId);
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }

        Book book = bookMapper.selectById(chapter.getBookId());
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }

        if (chapter.getIsVip() && !chapter.getIsFree()) {
            User user = new User();
            user.setId(userId);
            user.setVipLevel(1);

            if (user.getVipLevel() == 0) {
                throw new BusinessException("此章节为VIP章节，请开通会员");
            }
        }

        return chapter;
    }

    @Override
    public void createChapter(Chapter chapter) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getBookId, chapter.getBookId())
                .orderByDesc(Chapter::getChapterNum)
                .last("LIMIT 1");
        Chapter lastChapter = getOne(wrapper);

        chapter.setChapterNum(lastChapter == null ? 1 : lastChapter.getChapterNum() + 1);
        chapter.setWordCount(chapter.getContent() != null ? chapter.getContent().length() : 0);
        chapter.setCreatedAt(LocalDateTime.now());
        save(chapter);

        Book book = bookMapper.selectById(chapter.getBookId());
        if (book != null) {
            book.setChapterCount(book.getChapterCount() + 1);
            book.setWordCount(book.getWordCount() + chapter.getWordCount());
            bookMapper.updateById(book);
        }
    }

    @Override
    public void updateChapter(Chapter chapter) {
        chapter.setWordCount(chapter.getContent() != null ? chapter.getContent().length() : 0);
        updateById(chapter);
    }

    @Override
    public void deleteChapter(Long chapterId) {
        Chapter chapter = getById(chapterId);
        if (chapter != null) {
            removeById(chapterId);

            Book book = bookMapper.selectById(chapter.getBookId());
            if (book != null) {
                book.setChapterCount(Math.max(0, book.getChapterCount() - 1));
                book.setWordCount(Math.max(0, book.getWordCount() - chapter.getWordCount()));
                bookMapper.updateById(book);
            }
        }
    }
}