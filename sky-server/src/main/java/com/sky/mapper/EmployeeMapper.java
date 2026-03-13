package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工
     * @param employee
     */
    void insert(Employee employee);

    /**
     * 分页查询员工
     * @param name 员工姓名（模糊查询）
     * @return 员工列表
     */
    List<Employee> query(String name);

    /**
     * 根据id更新员工信息
     * @param employee 员工对象
     */
    void update(Employee employee);

    /**
     * 根据id查询员工
     * @param id 员工id
     * @return 员工对象
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

}
