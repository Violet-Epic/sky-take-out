package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.exception.AddressBookBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地址簿服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    private final AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     */
    @Override
    public void add(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        
        // 如果是第一个地址，设置为默认
        List<AddressBook> list = addressBookMapper.getByUserId(userId);
        if (list.isEmpty()) {
            addressBook.setIsDefault(1);
        } else if (addressBook.getIsDefault() == null) {
            addressBook.setIsDefault(0);
        }
        
        addressBookMapper.insert(addressBook);
        log.info("新增地址: {}", addressBook);
    }

    /**
     * 查询登录用户所有地址
     */
    @Override
    public List<AddressBook> list() {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> list = addressBookMapper.getByUserId(userId);
        log.info("查询用户地址列表, userId={}, 共{}条", userId, list.size());
        return list;
    }

    /**
     * 根据id查询地址
     */
    @Override
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        log.info("查询地址: {}", addressBook);
        return addressBook;
    }

    /**
     * 更新地址
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
        log.info("更新地址: {}", addressBook);
    }

    /**
     * 根据id删除地址
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
        log.info("删除地址: id={}", id);
    }

    /**
     * 设置默认地址
     */
    @Override
    public void setDefault(Long id) {
        // 1. 查询地址是否存在
        AddressBook addressBook = addressBookMapper.getById(id);
        if (addressBook == null) {
            throw new AddressBookBusinessException("地址不存在");
        }
        
        // 2. 清除当前用户所有默认地址
        Long userId = BaseContext.getCurrentId();
        addressBookMapper.clearDefaultByUserId(userId);
        
        // 3. 设置新的默认地址
        addressBookMapper.setDefault(id);
        log.info("设置默认地址: id={}", id);
    }

    /**
     * 查询默认地址
     */
    @Override
    public AddressBook getDefault() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getDefaultByUserId(userId);
        log.info("查询默认地址: {}", addressBook);
        return addressBook;
    }
}
