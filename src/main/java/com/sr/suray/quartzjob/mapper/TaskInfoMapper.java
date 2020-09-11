package com.sr.suray.quartzjob.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sr.suray.quartzjob.bean.TaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName TaskInfoMapper
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/4 下午5:43
 **/
@Mapper
@Repository
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {
}
