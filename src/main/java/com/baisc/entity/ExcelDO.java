package com.baisc.entity;

import com.baisc.annonate.ExcelOrder;
import lombok.Data;

/**
 * @author 橙子
 * @date 2020/1/12 10:25 下午
 */
@Data
public class ExcelDO {
    @ExcelOrder(order = 1)
    private String country;
    @ExcelOrder(order = 2)
    private String city;
    @ExcelOrder(order = 3)
    private String countryEng;
    @ExcelOrder(order = 4)
    private String province;
    @ExcelOrder(order = 5)
    private String author;
    @ExcelOrder(order = 6)
    private String lon;
    @ExcelOrder(order = 7)
    private String lat;
}
