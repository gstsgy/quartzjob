package com.sr.suray.quartzjob.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sr.suray.quartzjob.Service.TaskService;
import com.sr.suray.quartzjob.bean.ResponseBean;
import com.sr.suray.quartzjob.bean.TaskInfo;
import com.sr.suray.quartzjob.bean.UserBeanVO;
import com.sr.suray.quartzjob.mapper.UserMapper;
import com.sr.suray.quartzjob.util.Conf;
import com.sr.suray.quartzjob.util.Encrypt;
import com.sr.suray.quartzjob.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @ClassName JobController
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/7 下午4:46
 **/
@RestController
@RequestMapping("job")
public class JobController {

    private Integer initflag;


    private String passWord;

    @Autowired
    private ResponseBean responseBean ;


    @Autowired
    private TaskService taskService;

    private int getInitflag(){
        if(initflag!=null){
            return initflag;
        }else{
            synchronized (this){
                if(initflag!=null){
                    return initflag;
                }
                initflag = (int) Conf.getByKey("job.initflag");
            }
        }
        return initflag;
    }

    private String getPassWord(){
        if(passWord!=null){
            return passWord;
        }else{
            synchronized (this){
                if(passWord!=null){
                    return passWord;
                }
                passWord = (String) Conf.getByKey("job.userpw");
            }
        }
        return passWord;
    }





    @GetMapping("jobs")
    public ResponseBean query(String name) {

        return taskService.query(name);
    }

    @PostMapping("job")
    public ResponseBean add(@RequestBody TaskInfo taskInfo) {

        return taskService.add(taskInfo);
    }

    @PutMapping("job")
    public ResponseBean update(@RequestBody TaskInfo taskInfo) {

        return taskService.update(taskInfo);
    }

    @PutMapping("jobstatus")
    public ResponseBean changeStatus(@RequestBody TaskInfo taskInfo) {

        return taskService.changeStatus(taskInfo);
    }

    @PutMapping("jobcron")
    public ResponseBean updateCron(@RequestBody TaskInfo taskInfo, String opername) {

        return taskService.updateCron(taskInfo);
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

    @GetMapping("sysstate")
    public ResponseBean getSystemStats() {

        return responseBean.getSuccess(getInitflag());
    }


    @PostMapping("login")
    public ResponseBean login(@RequestBody UserBeanVO userBeanVO) {

        if(userBeanVO==null){
            return responseBean.getError("参数不可为空");
        }
        QueryWrapper<UserBeanVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserBeanVO::getUserName,userBeanVO.getUserName());
        UserBeanVO userBeanVO1 = userMapper.selectOne(queryWrapper);
        if(userBeanVO1==null){
            return responseBean.getError("用户名不存在！");
        }
        if(userBeanVO1.getPassWord().equals(Encrypt.encryptToMD5(userBeanVO.getPassWord()))){
            return responseBean.getSuccess(JWTUtil.getToken(null));
        }
        return responseBean.getError("密码错误！");
    }
    @Autowired
    UserMapper userMapper;

    @PutMapping("password")
    public ResponseBean changePW(String oldpw,String newpw) {

        if(oldpw==null||newpw==null){
            return responseBean.getError("参数不可为空");
        }
        if(getPassWord().equals(Encrypt.encryptToMD5(oldpw))){
            Map map = new HashMap<>();
            map.put("job.userpw",Encrypt.encryptToMD5(newpw));
            map.put("job.initflag",1);
            Conf.saveOrUpdateByKey(map);

            return responseBean.getSuccess(true);
        }
        return responseBean.getError("原密码错误！");
    }

    public static void main(String[] args) {
        System.out.println(Encrypt.encryptToMD5("wms2020."));
    }
}
