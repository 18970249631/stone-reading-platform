package com.stonereading.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("market_product")
public class MarketProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String productType;

    private Long contentId;

    private Long sellerId;

    private String sellerName;

    private String title;

    private String description;

    private String coverUrl;

    private String tags;

    private BigDecimal price;

    private String transactionType;

    private BigDecimal sharingRatio;

    private Integer salesCount;

    private Integer viewCount;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
