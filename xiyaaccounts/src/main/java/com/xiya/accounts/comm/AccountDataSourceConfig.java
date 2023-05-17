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
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@Primary
@MapperScan(basePackages = "com.xiya.accounts.mapper.account",sqlSessionFactoryRef = "accountSqlSessionFactory")
public class AccountDataSourceConfig {
    /**
     * 配置数据源
     *
     * @return
     */
    @Bean(name = "accountDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.account")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 配置 sql 会话工厂
     */
    @Bean(name = "accountSqlSessionFactory")
    @Primary
    public SqlSessionFactory sessionFactory(@Qualifier("accountDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        return bean.getObject();
    }

    /**
     * 事务管理器
     */
    @Bean(name = "accountTransactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("accountDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "accountSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("accountSqlSessionFactory") SqlSessionFactory sessionFactory) {
        return new SqlSessionTemplate(sessionFactory);
    }
}

