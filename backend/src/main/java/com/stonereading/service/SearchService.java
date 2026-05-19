package com.stonereading.service;

import java.util.List;
import java.util.Map;

public interface SearchService {

    Map<String, Object> searchBooks(String keyword, int page, int size);

    Map<String, Object> searchChapters(Long bookId, String keyword);

    void indexBook(Long bookId);

    void deleteIndex(Long bookId);
}
