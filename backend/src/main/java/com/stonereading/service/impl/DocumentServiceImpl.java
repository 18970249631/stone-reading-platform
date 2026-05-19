package com.stonereading.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonereading.entity.Document;
import com.stonereading.mapper.DocumentMapper;
import com.stonereading.service.DocumentService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Override
    public Document uploadDocument(Long userId, String fileName, String fileUrl, String fileType, Long fileSize) {
        Document document = new Document();
        document.setUserId(userId);
        document.setFileName(fileName);
        document.setFileUrl(fileUrl);
        document.setFileType(fileType);
        document.setFileSize(fileSize);
        document.setStatus(0);
        save(document);
        return document;
    }

    @Override
    @Async
    public String parseDocument(Long documentId) {
        Document document = getById(documentId);
        if (document == null) {
            return null;
        }

        try {
            document.setStatus(1);
            updateById(document);

            String content = extractTextFromFile(document.getFileUrl(), document.getFileType());

            document.setMarkdownContent(content);
            document.setParseResult("SUCCESS");
            document.setStatus(2);
            updateById(document);

            return content;
        } catch (Exception e) {
            document.setParseResult("FAILED: " + e.getMessage());
            document.setStatus(-1);
            updateById(document);
            return null;
        }
    }

    @Override
    public Document getDocumentDetail(Long documentId) {
        return getById(documentId);
    }

    private String extractTextFromFile(String fileUrl, String fileType) throws IOException {
        if (!StringUtils.hasText(fileUrl)) {
            return "";
        }

        Path path = Paths.get(fileUrl);
        if (!Files.exists(path)) {
            return "";
        }

        String lowerType = fileType != null ? fileType.toLowerCase() : "";

        if (lowerType.contains("txt") || lowerType.contains("text")) {
            return Files.readString(path);
        }

        if (lowerType.contains("pdf")) {
            return extractTextFromPdf(path);
        }

        if (lowerType.contains("doc") || lowerType.contains("word")) {
            return extractTextFromDocx(path);
        }

        return "[不支持的文件格式]";
    }

    private String extractTextFromPdf(Path path) {
        StringBuilder text = new StringBuilder();
        try (InputStream is = Files.newInputStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            return "[PDF解析失败]";
        }
        return text.toString();
    }

    private String extractTextFromDocx(Path path) {
        return "[DOCX格式解析 - 需要Apache POI库支持]";
    }
}