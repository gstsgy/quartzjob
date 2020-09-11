package com.sr.suray.quartzjob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class QuartzjobApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartzjobApplication.class, args);
    }
    // 添加RestTemplate bean以便使用时自动注入
    @Autowired
    private RestTemplateBuilder builder;
    // 使用RestTemplateBuilder来实例化RestTemplate对象，spring默认已经注入了RestTemplateBuilder实例
    @Bean
    public RestTemplate restTemplate() {
        return builder.setConnectTimeout(Duration.ofSeconds(8)).setReadTimeout(Duration.ofSeconds(8)).build();
    }

}
