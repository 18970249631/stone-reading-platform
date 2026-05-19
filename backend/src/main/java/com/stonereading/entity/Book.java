package com.stonereading.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private Long authorId;

    private String authorName;

    private String category;

    private String coverUrl;

    private String description;

    private Integer status;

    private Boolean isPaid;

    private BigDecimal price;

    private Integer coinPrice;

    private Long viewCount;

    private Long likeCount;

    private Integer chapterCount;

    private Integer wordCount;

    private String tags;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}