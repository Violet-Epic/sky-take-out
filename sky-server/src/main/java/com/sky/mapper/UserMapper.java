package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 新增用户
     */
    void insert(User user);

    /**
     * 统计某个日期范围内的新增用户
     */
    @Select("SELECT COUNT(*) FROM user WHERE create_time BETWEEN #{begin} AND #{end}")
    Integer countNewUserByDate(LocalDateTime begin, LocalDateTime end);

    /**
     * 统计某个时间点之前的用户总量
     */
    @Select("SELECT COUNT(*) FROM user WHERE create_time < #{endTime}")
    Integer countTotalUserBeforeDate(LocalDateTime endTime);
}
