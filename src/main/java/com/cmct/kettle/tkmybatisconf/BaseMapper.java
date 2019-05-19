/**
 * Copyright (c) @2016,cmct 版权所有
 */
package com.cmct.kettle.tkmybatisconf;

import org.springframework.data.repository.NoRepositoryBean;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T>, Serializable {



}
