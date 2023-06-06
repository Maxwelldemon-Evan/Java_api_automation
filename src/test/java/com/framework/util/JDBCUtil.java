package com.framework.util;


import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JDBCUtil {
    /**
     * 数据库连接
     * @return 连接对象
     */
    public static Connection getConnect(){
        String url ="jdbc:mysql://47.113.180.81/yami_shops?useUnicode=true&characterEncoding=utf-8&useSSL=true";
        String user = "lemon";
        String password = "lemon123";
        Connection connection = null;
        try {
            connection =DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    /**
     * 查绚单个结果字段
     * @param sql 要执行的sql语句
     * @return  查绚的结果
     */
    public static Object querySingleData(String sql){
        Connection conn = getConnect();
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
     * 查绚单条数据或者多条数据的方法
     * @param sql sql执行语句
     * @return 返回的结果
     */
    public static List<Map<String, Object>> queryDatas(String sql){
        Connection conn = getConnect();
        QueryRunner queryRunner = new QueryRunner();
        List<Map<String, Object>> result = null;
        try {
            result = queryRunner.query(conn,sql,new MapListHandler());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
