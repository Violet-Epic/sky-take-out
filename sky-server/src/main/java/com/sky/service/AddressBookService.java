package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

/**
 * 地址簿服务接口
 */
public interface AddressBookService {

    /**
     * 新增地址
     */
    void add(AddressBook addressBook);

    /**
     * 查询登录用户的所有地址
     */
    List<AddressBook> list();

    /**
     * 根据id查询地址
     */
    AddressBook getById(Long id);

    /**
     * 更新地址
     */
    void update(AddressBook addressBook);

    /**
     * 删除地址
     */
    void deleteById(Long id);

    /**
     * 设置默认地址
     */
    void setDefault(Long id);

    /**
     * 查询默认地址
     */
    AddressBook getDefault();
}
