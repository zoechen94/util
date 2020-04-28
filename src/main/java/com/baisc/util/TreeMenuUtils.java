package com.baisc.util;

import com.baisc.entity.MenuVO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaLan
 * @date 2020/4/14 0014 22:35
 * 树形菜单
 **/
public class TreeMenuUtils {
    private static List<MenuVO> menuList=new ArrayList<>();
    static {
        menuList.add(new MenuVO(1,null,"执法考评"));
        menuList.add(new MenuVO(2,1,"日常考评"));
        menuList.add(new MenuVO(5,1,"行政考评"));
        menuList.add(new MenuVO(4,2,"警情考评"));
        menuList.add(new MenuVO(6,4,"刑事考评"));
        menuList.add(new MenuVO(3,1,"抽案考评"));
        menuList.add(new MenuVO(7,null,"一级菜单"));
    }


    public static List<MenuVO> getMenuList(){
        //总菜单，出一级菜单，一级菜单没有父Id
        List<MenuVO> rootMenu =  menuList.stream().filter(m -> m.getParentId()==null).collect(Collectors.toList());
        //遍历
        rootMenu.stream().forEach(r->r.setChild(getChild(r.getId(),menuList)));
        return rootMenu;
    }

    //获取子节点；menuList就是所有的数据
    public static List<MenuVO> getChild(Integer id,List<MenuVO> menuList){
        List<MenuVO> child=new ArrayList<>();
        menuList.stream().forEach(m->{
            if(m.getParentId()!=null){
                if(m.getParentId().equals(id)){ //如果父亲ID和该id相等，就是他的孩子
                    child.add(m);
                }
            }
        });

        if(child.size()==0){
            return null;
        }

        //子菜单还有子菜单，利用递归，只有当儿子是0的时候才停止
        child.stream().forEach(c->{
            c.setChild(getChild(c.getId(),menuList));
        });

        return child;
    }
    public static void  main(String[] args){

       List<MenuVO> list=getMenuList();
       int k=0;
       for(int i=0;i<list.size();i++){
           print(k+1,list.get(i));
       }
    }

    public static void print(int k,MenuVO m){
        for(int level=1;level<k;level++){
            System.out.print("  ");
        }
        System.out.println(m.getName());
        if(m.getChild()!=null&&m.getChild().size()!=0){
            m.getChild().stream().forEach(c->{
                print(k+1,c);
            });
        }
    }
}
