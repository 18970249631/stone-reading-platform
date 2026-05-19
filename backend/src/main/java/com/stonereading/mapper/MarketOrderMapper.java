package com.stonereading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stonereading.entity.MarketOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MarketOrderMapper extends BaseMapper<MarketOrder> {
}
