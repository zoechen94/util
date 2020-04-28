package com.baisc.entity;

import com.baisc.annonate.Excel;
import lombok.Data;

/**
 * @author 橙子
 * @date 2020/1/12 10:25 下午
 */
@Data
public class ExcelDO {
    @Excel(order = 1)
    private String country;
    @Excel(order = 2)
    private String city;
    @Excel(order = 3)
    private String countryEng;
    @Excel(order = 4)
    private String province;
    @Excel(order = 5)
    private String author;
    @Excel(order = 6)
    private String lon;
    @Excel(order = 7)
    private String lat;
}
