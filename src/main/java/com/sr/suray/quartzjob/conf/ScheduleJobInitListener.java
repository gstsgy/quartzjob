package com.sr.suray.quartzjob.conf;

import com.sr.suray.quartzjob.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @ClassName ScheduleJobInitListener
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/4 下午5:21
 **/
@Component
@Order(value = 1)
public class ScheduleJobInitListener implements CommandLineRunner {

    @Autowired
    private TaskService scheduleJobService;

    @Override
    public void run(String... arg0) throws Exception {
        try {
            scheduleJobService.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
