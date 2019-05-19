package com.cmct.kettle.web.conf;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;

/**
 * @author Aswords
 * @Date 2017/2/13
 * @since v0.1
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true)
public class DataSourceConf {

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        // 注册自己项目中使用的通用Mapper接口，这里没有默认值，必须手动注册
        mapperScannerConfigurer.setBasePackage("com.cmct.kettle.core.mapper");
        mapperScannerConfigurer.setMarkerInterface(BaseMapper.class);
        //配置完成后，执行下面的操作
        return mapperScannerConfigurer;
    }


}
