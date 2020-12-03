package com.sr.suray.quartzjob;

import com.sr.suray.quartzjob.util.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
@ServletComponentScan
@SpringBootApplication
public class QuartzjobApplication {




    public static void main(String[] args) {
        SpringApplication.run(QuartzjobApplication.class, args);
    }
    // 添加RestTemplate bean以便使用时自动注入
    @Value("${job.passwd}")
    private String passwd;


    @Autowired
    private RestTemplateBuilder builder;

    // 使用RestTemplateBuilder来实例化RestTemplate对象，spring默认已经注入了RestTemplateBuilder实例
    @Bean
    public RestTemplate restTemplate() {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCa9yaMZuvRGWiJ9YD4rUEF39588I89aE6H6208kNVm3qaOpLZMU5xXHKL1UsKDqjaoV5vJnban3Gu2hWpkHP/IKPB3MxUjTEsLOKkCt/8G0JEEtOd3LOr3SGo6b4ofWSAEFegfsd9F9BC3/q/Ag5TY18uTFBZ9mopDYGZfQCZkuwIDAQAB";

        return builder.setConnectTimeout(Duration.ofSeconds(8)).
                setReadTimeout(Duration.ofSeconds(8)).defaultHeader("password", Encrypt.encryptToRSA(passwd,publicKey)).build();
    }

}
