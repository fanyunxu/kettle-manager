/**
 */
package io.fanyun.kettle.tkmybatisconf;

import org.springframework.data.repository.NoRepositoryBean;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T>, Serializable {



}
