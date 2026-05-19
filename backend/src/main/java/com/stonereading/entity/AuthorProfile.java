package com.stonereading.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("author_profile")
public class AuthorProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String penName;

    private String realName;

    private String idCard;

    private String avatar;

    private String bio;

    private String skills;

    private String works;

    private Integer verifyStatus;

    private String verifyReason;

    private Long verifyAdminId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifiedAt;

    private Integer bookCount;

    private Integer totalWordCount;

    private BigDecimal totalEarnings;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
