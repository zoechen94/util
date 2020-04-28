package com.baisc.entity;

import lombok.Data;

import java.util.List;

/**
 * @author yaLan
 * @date 2020/4/14 0014 22:36
 **/
@Data
public class MenuVO {
    private Integer id;
    private Integer parentId;
    private String name;
    private List<MenuVO> child;
    public MenuVO(){}
    public MenuVO(Integer id,Integer parentId,String name){
        this.id=id;
        this.parentId=parentId;
        this.name=name;
    }
}
