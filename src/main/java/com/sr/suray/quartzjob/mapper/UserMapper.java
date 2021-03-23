package com.sr.suray.quartzjob.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sr.suray.quartzjob.bean.UserBeanVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Classname UserMapper
 * @Description TODO
 * @Date 2021/3/23 下午5:37
 * @Created by guyue
 */
@Repository
@Mapper
public interface UserMapper extends BaseMapper<UserBeanVO> {
}
