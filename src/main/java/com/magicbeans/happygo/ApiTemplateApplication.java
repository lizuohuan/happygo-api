package com.magicbeans.happygo;

import com.magicbeans.happygo.redis.JdkRedisTemplate;
import com.magicbeans.happygo.redis.ObjectRedisTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author :Jason
 * @date ：2017/8/28 0028
 * @description
 **/
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan("com.magicbeans")
@EnableScheduling
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 4800)
public class ApiTemplateApplication extends SpringBootServletInitializer implements ApplicationListener<ApplicationReadyEvent> {


    /**
     * 进行数据初始化
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {


    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return  builder.sources(ApiTemplateApplication.class);
    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy(){
        return new CookieHttpSessionStrategy();
    }


    @Bean
    public ObjectRedisTemplate objectRedisTemplate(RedisConnectionFactory connectionFactory){
        return new ObjectRedisTemplate(connectionFactory);
    }

    @Bean
    public JdkRedisTemplate jdkRedisTemplate(RedisConnectionFactory connectionFactory){
        return  new JdkRedisTemplate(connectionFactory);
    }


    public static void main(String[] args) {
        SpringApplication.run(ApiTemplateApplication.class,args);
    }
}
