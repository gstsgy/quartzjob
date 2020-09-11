package com.sr.suray.quartzjob.controller;

import com.sr.suray.quartzjob.Service.TaskService;
import com.sr.suray.quartzjob.bean.ResponseBean;
import com.sr.suray.quartzjob.bean.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName JobController
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/7 下午4:46
 **/
@RestController
@RequestMapping("job")
public class JobController {
    @Autowired
    private TaskService taskService;

    @GetMapping("jobs")
    public ResponseBean query(String name) {

        return taskService.query(name);
    }

    @PostMapping("job")
    public ResponseBean add(@RequestBody TaskInfo taskInfo, String opername) {

        return taskService.add(taskInfo, opername);
    }

    @PutMapping("job")
    public ResponseBean update(@RequestBody TaskInfo taskInfo, String opername) {

        return taskService.update(taskInfo, opername);
    }

    @PutMapping("jobstatus")
    public ResponseBean changeStatus(@RequestBody TaskInfo taskInfo, String opername) {

        return taskService.changeStatus(taskInfo, opername);
    }

    @PutMapping("jobcron")
    public ResponseBean updateCron(@RequestBody TaskInfo taskInfo, String opername) {

        return taskService.updateCron(taskInfo, opername);
    }

    @PutMapping("run")
    public ResponseBean run(@RequestBody TaskInfo task) {
        return taskService.run(task);
    }

    @DeleteMapping("jobs")
    public ResponseBean delete(@RequestBody List<TaskInfo> taskInfos) {
        return taskService.delete(taskInfos);
    }


    @GetMapping("t1")
    public ResponseBean test() {
        return new ResponseBean().getSuccess("荷兰咯");
    }
}
