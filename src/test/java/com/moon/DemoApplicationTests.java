//package com.moon;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.sql.DataSource;
//import java.sql.Array;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//class DemoApplicationTests {
//    @Autowired
//    DataSource dataSource;
//
//    @Test
//    void contextLoads() throws SQLException {
////        查看默认数据源
//        System.out.println("==>" + dataSource.getClass());
////        获取数据库连接
//        Connection connection = dataSource.getConnection();
//        System.out.println("=======>" + connection);
//        connection.close();
//
//        List<Map<String, List>> list = new ArrayList<Map<String, List>>();
//        List<Map<String, Object>> listobj = new ArrayList<Map<String, Object>>();
////需要键值对形式的数据时，应该使用HashMap
//        Map<String, List> map = new HashMap<String, List>();
//        Map<String, Object> mapObj = new HashMap<String, Object>();
//
//        mapObj.put("aaa", "wanan");//存储对象-构成对象
//        listobj.add(mapObj);//对象存入数组
//        map.put("a", listobj);//存储list-将数组构成对象
//        list.add(map);//存入数组
//
//        System.out.println("test==>" + list+" "+list.get(0).get("a"));
//    }
//
//}