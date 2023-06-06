package com.framework.util;

import com.framework.common.BaseTest;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


public class JDBCUtil2 {
    Logger logger = Logger.getLogger(JDBCUtil2.class);

    /**
     *连接数据库(可传入不同的数据库信息)
     * @param user 用户名
     * @param password 密码
     * @param url   数据库连接地址 jdbc:mysql://47.113.180.81/yami_shops?useUnicode=true&characterEncoding=utf-8&useSSL=true
     * @return 连接对象
     */
    public static Connection getConnect2(String user, String password, String url){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    /**
     * 查询单个结果字段
     * @param sql 要执行的sql语句
     * @param user 数据库用户名
     * @param password  密码
     * @param url   数据库连接地址
     * @return  查询结果
     */
    public static Object querySingleData(String sql, String user, String password, String url){
        Connection conn = getConnect2(user, password, url);
        QueryRunner queryRunner = new QueryRunner();
        Object result = null;
        try {
            result = queryRunner.query(conn,sql,new ScalarHandler<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 查询单条结果或者多条结果
     * @param sql   要执行的sql语句
     * @param user  数据库用户名
     * @param password  密码
     * @param url   数据库连接地址
     * @return  查询结果
     */
    public static List<Map<String, Object>> queryDatas(String sql, String user, String password, String url){
        Connection conn = getConnect2(user, password, url);
        QueryRunner queryRunner = new QueryRunner();
        List<Map<String, Object>> result = null;
        try {
            result = queryRunner.query(conn,sql,new MapListHandler());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 修改数据库操作（增、删、改）
     * @param sql
     * @param user
     * @param password
     * @param url
     * @return 修改是否成功(0:失败, 1:成功)
     */
    public static Object  updateDatas(String sql, String user, String password, String url){
        Connection conn = getConnect2(user, password, url);
        QueryRunner queryRunner = new QueryRunner();
        Object result = null;
        Object determine = 1;
        try {
            result = queryRunner.update(conn,sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (result == determine ){
            System.out.println("sql执行成功");
        }else {
            System.out.println("sql执行失败");
        }
        return result;
    }


    public static void main(String[] args) {
        String sql1 = "update books set name = '黄茂山的书3' where id =21;";
        String sql2 = "delete from books where id =22;";
        String sql3 = "insert into books  values(22, 'Java','F区1号架2层', '在库', '');";
        String user = "root";
        String password = "123456";
        String url = "jdbc:mysql://127.0.1/test?useUnicode=true&characterEncoding=utf-8&useSSL=true";
        updateDatas(sql3, user,password,url);
    }
}
