package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 地址簿 Mapper
 */
@Mapper
public interface AddressBookMapper {

    /**
     * 新增地址
     */
    @Insert("INSERT INTO address_book (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "VALUES (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AddressBook addressBook);

    /**
     * 查询用户所有地址
     */
    @Select("SELECT * FROM address_book WHERE user_id = #{userId} ORDER BY is_default DESC, id DESC")
    List<AddressBook> getByUserId(Long userId);

    /**
     * 根据id查询地址
     */
    @Select("SELECT * FROM address_book WHERE id = #{id}")
    AddressBook getById(Long id);

    /**
     * 更新地址
     */
    @Update("UPDATE address_book SET consignee = #{consignee}, phone = #{phone}, sex = #{sex}, " +
            "province_code = #{provinceCode}, province_name = #{provinceName}, " +
            "city_code = #{cityCode}, city_name = #{cityName}, " +
            "district_code = #{districtCode}, district_name = #{districtName}, " +
            "detail = #{detail}, label = #{label}, is_default = #{isDefault} " +
            "WHERE id = #{id}")
    void update(AddressBook addressBook);

    /**
     * 删除地址
     */
    @Delete("DELETE FROM address_book WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 设置默认地址（先清除该用户所有默认，再设置新的）
     */
    @Update("UPDATE address_book SET is_default = 0 WHERE user_id = #{userId}")
    void clearDefaultByUserId(Long userId);

    /**
     * 设置为默认地址
     */
    @Update("UPDATE address_book SET is_default = 1 WHERE id = #{id}")
    void setDefault(Long id);

    /**
     * 查询默认地址
     */
    @Select("SELECT * FROM address_book WHERE user_id = #{userId} AND is_default = 1")
    AddressBook getDefaultByUserId(Long userId);
}
