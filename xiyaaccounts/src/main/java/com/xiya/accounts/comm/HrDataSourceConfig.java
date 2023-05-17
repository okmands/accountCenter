package com.xiya.accounts.comm;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.xiya.accounts.mapper.hr",sqlSessionFactoryRef = "hrSqlSessionFactory")
public class HrDataSourceConfig {
    /**
     * 配置数据源
     */
    @Bean(name = "hrDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hr")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 配置 sql 会话工厂
     */
    @Bean(name = "hrSqlSessionFactory")
    public SqlSessionFactory sessionFactory(@Qualifier("hrDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        return bean.getObject();
    }

    /**
     * 事务管理器
     */
    @Bean(name = "hrTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("hrDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "hrSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("hrSqlSessionFactory") SqlSessionFactory sessionFactory) {
        return new SqlSessionTemplate(sessionFactory);
    }
}
