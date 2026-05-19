package com.stonereading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonereading.entity.Document;

public interface DocumentService extends IService<Document> {

    Document uploadDocument(Long userId, String fileName, String fileUrl, String fileType, Long fileSize);

    String parseDocument(Long documentId);

    Document getDocumentDetail(Long documentId);
}