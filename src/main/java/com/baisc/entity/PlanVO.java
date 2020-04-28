package com.baisc.entity;

import com.baisc.annonate.Excel;
import lombok.Data;

/**
 * @author yaLan
 * @date 2020/02/11 21:16
 **/
@Data
public class PlanVO {
    private Integer id;
    @Excel(order = 1)
    private String one;
    @Excel(order = 2)
    private String two;
    @Excel(order = 3)
    private String hour;
    @Excel(order = 4)
    private String author;
}
