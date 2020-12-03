package com.sr.suray.quartzjob.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sr.suray.quartzjob.bean.JobStatusEnum;
import com.sr.suray.quartzjob.bean.ResponseBean;
import com.sr.suray.quartzjob.bean.TaskInfo;
import com.sr.suray.quartzjob.mapper.TaskInfoMapper;
import com.sr.suray.quartzjob.util.QuartzManager;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @ClassName TaskService
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/4 下午5:22
 **/
@Service
public class TaskService {
    @Autowired
    private TaskInfoMapper taskMapper;

    @Autowired
    QuartzManager quartzManager;
    @Autowired
    private ResponseBean responseBean;

    public ResponseBean query(String name) {
        QueryWrapper query =  new QueryWrapper<TaskInfo>();
        if(name!=null&&!name.equals("")){
            query.like("job_name",name);
        }
        return responseBean.getSuccess(taskMapper.selectList(query));
    }

    public ResponseBean add(TaskInfo taskInfo){
        taskInfo.setJobStatus(0);

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        taskInfo.setCreateTime(dtf2.format(LocalDateTime.now()));
        taskInfo.setUpdateTime(dtf2.format(LocalDateTime.now()));
        //quartzManager.addJob(taskInfo);
        taskMapper.insert(taskInfo);
        return responseBean.getSuccess(true);
    }

    public ResponseBean update(TaskInfo taskInfo){
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        taskInfo.setUpdateTime(dtf2.format(LocalDateTime.now()));
        return responseBean.getSuccess(taskMapper.updateById(taskInfo)>0);
    }

    public ResponseBean changeStatus(TaskInfo taskInfo) {

        if (JobStatusEnum.STOP.getCode().equals(taskInfo.getJobStatus())) {
            try {
                quartzManager.deleteJob(taskInfo);
            } catch (SchedulerException e) {
                throw  new RuntimeException(e.getMessage());
            }
            taskInfo.setJobStatus(JobStatusEnum.STOP.getCode());
        } else {
            taskInfo.setJobStatus(JobStatusEnum.RUNNING.getCode());
            quartzManager.addJob(taskInfo);
        }
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        taskInfo.setUpdateTime(dtf2.format(LocalDateTime.now()));
        return responseBean.getSuccess(taskMapper.updateById(taskInfo)>0);
    }


    public ResponseBean updateCron(TaskInfo taskInfo)  {

        if (JobStatusEnum.RUNNING.getCode().equals(taskInfo.getJobStatus())) {
            try {
                quartzManager.updateJobCron(taskInfo);
            } catch (SchedulerException e) {
                throw  new RuntimeException(e.getMessage());
            }
        }
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        taskInfo.setUpdateTime(dtf2.format(LocalDateTime.now()));
        return responseBean.getSuccess(taskMapper.updateById(taskInfo)>0);
    }

    public ResponseBean run(TaskInfo task)  {
        try {
            quartzManager.runJobNow(task);
        } catch (SchedulerException e) {
            throw  new RuntimeException(e.getMessage());
        }
        return responseBean.getSuccess(true);
    }

    public ResponseBean delete(List<TaskInfo> taskInfos){
        if(taskInfos.size()>0){
            taskInfos.forEach(item->{
                if (JobStatusEnum.STOP.getCode().equals(item.getJobStatus())) {
                    try {
                        quartzManager.deleteJob(item);
                    } catch (SchedulerException e) {
                        throw  new RuntimeException(e.getMessage());
                    }
                    // taskInfo.setStatus(JobStatusEnum.STOP.getCode());
                    taskMapper.deleteById(item.getId());
                }
            });
            return responseBean.getSuccess(true);
        }
        return responseBean.getSuccess(false);
    }

    public void  init(){
        QueryWrapper query =  new QueryWrapper<TaskInfo>();
        List<TaskInfo> jobList = taskMapper.selectList(query);
        for (TaskInfo task : jobList) {
            if (JobStatusEnum.RUNNING.getCode().equals(task.getJobStatus())) {
                quartzManager.addJob(task);
            }
        }
    }



    /*public TaskInfo get(Long id) {
        return taskMapper.selectById(id);
    }

    public ResponseBean list() {

        List<TaskInfo> list = taskMapper.selectList(query.getDescription());
        //取记录总条数
        PageInfo<TaskVO> pageInfo = new PageInfo<TaskVO>(list);
        long total = pageInfo.getTotal();
        //创建一个返回值对象
        ResponseBean result = new ResponseBean();
        result.setData(list);
        result.setCount(total);
        return result;
    }

    public int save(TaskInfo task) {
        task.setStatus(task.getStatus());

        task.setUpdateUser(((UserDO)SecurityUtils.getSubject().getPrincipal()).getUsername());
        task.setUpdateTime(new Date());
        return taskMapper.updateById(task);
    }


    public int update(TaskInfo task) {
        return taskMapper.updateById(task);
    }

    public int remove(Long id) {
        try {
            TaskDO task = get(id);
            quartzManager.deleteJob(task);
            return taskMapper.remove(id);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public int removeBatch(Long[] ids) {
        for (Long id : ids) {
            try {
                TaskDO task = get(id);
                quartzManager.deleteJob(task);
            } catch (SchedulerException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return taskMapper.removeBatch(ids);
    }

    public void initSchedule() throws SchedulerException {
        // 这里获取任务信息数据
        List<TaskInfo> jobList = taskMapper.selectList();
        for (TaskInfo task : jobList) {
            if (JobStatusEnum.RUNNING.getCode().equals(task.getStatus())) {
                quartzManager.addJob(task);
            }
        }
    }

    public void changeStatus(Long jobId, String jobStatus) throws SchedulerException {
        TaskInfo task = get(jobId);
        if (task == null) {
            return;
        }
        if (JobStatusEnum.STOP.getCode().equals(jobStatus)) {
            quartzManager.deleteJob(task);
            task.setStatus(JobStatusEnum.STOP.getCode());
        } else {
            task.setStatus(JobStatusEnum.RUNNING.getCode());
            quartzManager.addJob(task);
        }
        update(task);
    }


    public void updateCron(Long jobId) throws SchedulerException {
        TaskInfo task = get(jobId);
        if (task == null) {
            return;
        }
        if (JobStatusEnum.RUNNING.getCode().equals(task.getStatus())) {
            quartzManager.updateJobCron(task);
        }
        update(task);
    }


    public void run(TaskInfo task) throws SchedulerException {
        quartzManager.runJobNow(task);
    }*/

}
