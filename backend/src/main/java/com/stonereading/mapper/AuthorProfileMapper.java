package com.stonereading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stonereading.entity.AuthorProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthorProfileMapper extends BaseMapper<AuthorProfile> {
}
