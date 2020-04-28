package com.baisc.util;


import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * @author 橙子
 * @date 2020/1/11 5:27 下午
 * 数据库连接
 * 1.class.forName()加载驱动
 * 2.DriverManager.getConnection()获取数据库连接对象
 * 3.根据sql,有两种方式Statement,PreparedStatement
 * 4.执行sql处理结果集，如果有参数就设置参数
 * 5.关闭结果集，关闭会话，关闭资源
 */
@Slf4j
public class DBUtils {


    /**
     * 得到连接
     * @param driver
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static Connection getConnection(String driver,String url,String username,String password){

        try{

            Class.forName(driver);
            Connection connection8= DriverManager.getConnection(url,username,password);
            return connection8;
        }catch (ClassNotFoundException e){
           log.error("驱动名字找不到");
        }catch (SQLException e){
            log.error("找不到连接");
        }
        return null;
    }

    /**
     * 得到stmt
     * @param conn
     * @return
     */
    public static Statement getStatement(Connection conn){
        Statement stmt=null;
        try {
            stmt=conn.createStatement();
        }catch (SQLException e){
            log.error("得到stmt出错");
        }
        return stmt;
    }


    /**
     * 得到stmt重载
     * @param conn
     * @param sql
     * @return
     */
    public static Statement getStatement(Connection conn,String sql){
        Statement stmt=null;
        try {
            stmt=conn.prepareStatement(sql);
        }catch (SQLException e){
            log.error("得到stmt出错");
        }
        return stmt;
    }

    /**
     *关闭资源
     * @param conn
     * @param stmt
     * @param rs
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs){
        if(rs!=null){
            try{
                rs.close();
            }catch (SQLException e){
                log.error("关闭结果集对象错误");
            }
        }
        if(stmt!=null){
            try{
                stmt.close();
            }catch (SQLException e){
                log.error("关闭stmt出错");
            }
        }
        if(conn!=null){
            try{
                conn.close();
            }catch (SQLException e){
                log.error("关闭连接出错");
            }
        }

    }


    public static void main(String[] args) throws SQLException {
           Connection conn=getConnection("com.mysql.jdbc.Driver","jdbc:mysql://127.0.0.1:3306/cyl","root","");
//           createTable(conn);//建表
//           insert(conn);
//           update(conn,2);
//        delete(conn,1);
        search(conn);


    }

    public static void createTable(Connection conn) throws SQLException {
        String createTest="CREATE TABLE IF NOT EXISTS `book`(\n" +
                "   `id` INT UNSIGNED AUTO_INCREMENT,\n" +
                "   `title` VARCHAR(100) NOT NULL,\n" +
                "   `author` VARCHAR(40) NOT NULL,\n" +
                "   `date` DATE,\n" +
                "   PRIMARY KEY ( `id` )\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        Statement stmt=getStatement(conn,createTest);
        stmt.execute(createTest);
        close(null,stmt,null);
    }

    /**
     * 只有PreparedStatement有set方法，基础Statement没有
     * @param conn
     * @throws SQLException
     */
    public static void insert(Connection conn) throws SQLException {
        String insert="insert into book(title,author,date) values(?,?,?)";
        PreparedStatement stmt= (PreparedStatement) getStatement(conn,insert);
        stmt.setString(1,"遮蔽的天空");
        stmt.setString(2,"鲍尔斯");
        stmt.setDate(3,new Date(new java.util.Date().getTime()));
        int i=stmt.executeUpdate();
        if(i!=0){
            log.debug("添加成功");
        }else{
            log.debug("没有成功");
        }
        stmt.close();
    }


    /**
     * 修改
     * @param conn
     * @param id 根据主键修改
     * @throws SQLException
     */
    public static void update(Connection conn,int id) throws SQLException {
        String update="update book set id="+(id+1)+",title=? where id=?";
        PreparedStatement stmt= (PreparedStatement) getStatement(conn,update);
        stmt.setString(1,"欲望的渎职");
        stmt.setInt(2,id);
        int i=stmt.executeUpdate();
        if(i!=0){
            log.debug("添加成功");
        }else{
            log.debug("没有成功");
        }
        stmt.close();
    }

    /**
     * 删除
     * @param conn
     * @param id 根据id删除
     * @throws SQLException
     */
    public static void delete(Connection conn,int id) throws SQLException {
        String delete="delete from book where id=?";
        PreparedStatement stmt= (PreparedStatement) getStatement(conn,delete);
        stmt.setInt(1,id);
        int i=stmt.executeUpdate();
        if(i!=0){
            log.debug("添加成功");
        }else{
            log.debug("没有成功");
        }
        stmt.close();
    }

    /**
     * 查询
     * @param conn
     * @throws SQLException
     */
    public static void search(Connection conn) throws SQLException {
        String select="select * from book where id>?";
        PreparedStatement stmt= (PreparedStatement) getStatement(conn,select);
        stmt.setInt(1,2);
        ResultSet rs=stmt.executeQuery();
        System.out.println("============================");
        //取出有多少个列，然后遍历结果集条数循环每一个列，从1开始，到该列结束
        int col=rs.getMetaData().getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= col; i++) {
                System.out.print(rs.getString(i) + "\t");
            }
            System.out.println("");
        }
        System.out.println("============================");
    }

}
