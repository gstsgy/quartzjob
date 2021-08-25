package com.sr.suray.quartzjob.util;

import com.sr.suray.quartzjob.conf.SslUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

/**
 * @ClassName JobClassUtil
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/7 下午3:39
 **/
@DisallowConcurrentExecution //作业不并发
@Component
public class HelloWorldJob implements Job {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        JobDataMap dataMap = arg0.getJobDetail().getJobDataMap();

       // System.out.println("url:"+dataMap.get("url"));
        if(dataMap.get("url").toString().startsWith("https")){
            try {
                SslUtils.ignoreSsl();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ResponseEntity<String> result=restTemplate.postForEntity(dataMap.get("url").toString(),null, String.class);
        System.out.println(dataMap.get("url").toString());

    }

}
