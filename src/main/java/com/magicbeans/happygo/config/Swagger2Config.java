package com.magicbeans.happygo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .globalOperationParameters(createTokenHeader());
    }


    public ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("魔豆互动接口测试环境")
                .description("接口测试工具")
                .contact("研发部")
                .version("1.0")
                .build();
    }

    /**
     * 创建Token的全局授权参数
     * @return
     */
    private  List<Parameter> createTokenHeader(){
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenPar.name("Authorization").description("授权Token").modelRef(new ModelRef("string")).parameterType("header").defaultValue("Bearer ").required(false).build();
        pars.add(tokenPar.build());
        return pars;
    }
}
