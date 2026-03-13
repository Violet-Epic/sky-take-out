package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对（使用 BCrypt）
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 1. DTO 属性复制到 Entity
        BeanUtils.copyProperties(employeeDTO, employee);

        // 2. 设置默认密码（123456，BCrypt 加密）
        employee.setPassword(passwordEncoder.encode("123456"));

        // 3. 设置状态（默认启用）
        employee.setStatus(StatusConstant.ENABLE);

        // 4. 设置创建时间和更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 5. 设置创建人和更新人（当前登录用户）
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        // 6. 插入数据库
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询员工
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    public PageResult pageQuery(EmployeePageQueryDTO dto) {
        // 1. 开启分页（只对下一条 SQL 生效）
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        // 2. 查询（PageHelper 自动拦截并加 LIMIT）
        Page<Employee> page = (Page<Employee>) employeeMapper.query(dto.getName());

        // 3. 封装结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 启用/禁用员工
     *
     * @param status 状态（1启用，0禁用）
     * @param id 员工id
     */
    public void startOrStop(Integer status, Long id) {
        // 1. 创建 Employee 对象，只设置需要更新的字段
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        // 2. 调用 Mapper 更新
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工
     *
     * @param id 员工id
     * @return 员工对象
     */
    public Employee getById(Long id) {
        return employeeMapper.getById(id);
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO 员工DTO
     */
    public void update(EmployeeDTO employeeDTO) {
        // 1. DTO 复制到 Entity
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        // 2. 设置更新时间和更新人
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());

        // 3. 调用 Mapper 更新（复用通用方法）
        employeeMapper.update(employee);
    }

}
