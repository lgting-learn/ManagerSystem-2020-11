package com.moon.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.*;

import static org.apache.coyote.http11.Constants.a;

@RestController
public class ClassifyDAO {
    @Autowired
    JdbcTemplate jdbcTemplate;

    //前端首页-顶部分类
    @GetMapping("/api/classify")
    public List<Map<String, Object>> apiFood() {
        String sql = "select * from elemensys.classify";
        List<Map<String, Object>> classify_list = jdbcTemplate.queryForList(sql);
        return classify_list;
    }

    //前端首页-所有店铺
    @GetMapping("/api/shop")
    public List<Map<String, Object>> apiShop() {
        String sql = "select * from elemensys.shop";
        List<Map<String, Object>> shop_list = jdbcTemplate.queryForList(sql);
        return shop_list;
    }

    //单间店铺详情
    @PostMapping("/api/shopSingle")
    public Object shopSingle(HttpServletRequest request) {
        String id = request.getParameter("id");
        String sql = "select * from elemensys.shop where id=" + id;
        System.out.println("单间店铺详情==>" + sql);
        Object obj = jdbcTemplate.queryForList(sql).get(0);
//        Object obj = jdbcTemplate.queryForList(sql).get(0).put("aaa",1);

        System.out.println("obj==>" + obj);
        return obj;
    }

    //店铺-左侧菜单详情
    @PostMapping("/api/shopFoodClassify")
    public List<Map<String, Object>> shopFoodClassify(HttpServletRequest request) {
        String restaurant_id = request.getParameter("restaurant_id");
        String sql = "select * from elemensys.shop_food_classify where restaurant_id=" + restaurant_id;
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        Object id = null;
        List<Map<String, Object>> resultsFood = null;
        Map<String, Object> mapFood = null;
        for (int i = 0; i < results.size(); i++) {//Iterator遍历结果集报错，后采用for循环解决
            id = results.get(i).get("id");
            sql = "select * from elemensys.food where category_id=" + id;
//将另一个数据库结果集赋值给另一个结果集的某一属性
            resultsFood = jdbcTemplate.queryForList(sql);
            if (resultsFood.size() != 0) {
                results.get(i).put("foods", resultsFood);
            }
        }

        System.out.println("左侧菜单详情==>" + results);
        return results;
    }

    //店铺-右侧 改变食品数量
    @PostMapping("/api/changeFoodsNum")
    public List<Map<String, Object>> changeFoodsNum(HttpServletRequest request) {
        String foodId = request.getParameter("id");
        String restaurantId = request.getParameter("restaurant_id");
        String buy_number = request.getParameter("buy_number");
        String sql = "update elemensys.food set buy_number='" + buy_number + "' where id=" + foodId;
        System.out.println( "改变食品数量==>"+sql);
        jdbcTemplate.update(sql);
//        sql = "select * from elemensys.food";
         sql = "select * from elemensys.shop_food_classify where restaurant_id=" + restaurantId;
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        Object id = null;
        List<Map<String, Object>> resultsFood = null;
        Map<String, Object> mapFood = null;
        for (int i = 0; i < results.size(); i++) {//Iterator遍历结果集报错，后采用for循环解决
            id = results.get(i).get("id");
            sql = "select * from elemensys.food where category_id=" + id;
//将另一个数据库结果集赋值给另一个结果集的某一属性
            resultsFood = jdbcTemplate.queryForList(sql);
            if (resultsFood.size() != 0) {
                results.get(i).put("foods", resultsFood);
            }
        }
        return results;
    }
}
