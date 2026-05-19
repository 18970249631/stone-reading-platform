package com.stonereading.controller;

import com.stonereading.common.Result;
import com.stonereading.entity.Document;
import com.stonereading.entity.User;
import com.stonereading.service.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/doc")
public class DocumentController {

    private final DocumentService documentService;

    @Value("${file.upload.path}")
    private String uploadPath;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadDocument(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase() : "";

        String fileName = UUID.randomUUID().toString() + "." + extension;
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        String fileUrl = filePath.toString();
        Document document = documentService.uploadDocument(
                user.getId(),
                originalFilename,
                fileUrl,
                extension,
                file.getSize()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("documentId", document.getId());
        result.put("fileName", originalFilename);
        result.put("fileUrl", fileUrl);

        return Result.success(result);
    }

    @PostMapping("/parse/{documentId}")
    public Result<Void> parseDocument(@PathVariable Long documentId) {
        documentService.parseDocument(documentId);
        return Result.success();
    }

    @GetMapping("/detail/{documentId}")
    public Result<Document> getDocumentDetail(@PathVariable Long documentId) {
        Document document = documentService.getDocumentDetail(documentId);
        return Result.success(document);
    }

    @GetMapping("/content/{documentId}")
    public Result<String> getDocumentContent(@PathVariable Long documentId) {
        Document document = documentService.getDocumentDetail(documentId);
        if (document == null) {
            return Result.error("文档不存在");
        }
        return Result.success(document.getMarkdownContent());
    }
}