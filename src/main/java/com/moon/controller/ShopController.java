package com.moon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

@Controller
public class ShopController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    //进入店铺列表
    @RequestMapping("/shopList")
    public String shopList(Model model) {
        String sql = "select * from elemensys.shop order by id asc";
        List<Map<String, Object>> shop_list = jdbcTemplate.queryForList(sql);
        model.addAttribute("shopList", shop_list);
        System.out.println("进入店铺列表==>"+shop_list);
        return "/shop/shopList";
    }

    //进入添加店铺页面
    @RequestMapping("/addShop")
    public String addShop() {
        System.out.println("进入添加店铺");
        return "/shop/addShop";
    }

    //删除数据
    @GetMapping("/shopDelete/{id}")
    public String deleteShop(@PathVariable("id") Integer id) {
        String sql = "delete from elemensys.shop where id=?";
        jdbcTemplate.update(sql, id);
        return "redirect:/shopList";//重定向的时候相当于再跑一遍上面获取数据的方法，达到数据刷新的效果
    }


    //    跳转到更新页面
    @GetMapping("/shopUpdate/{id}")
    public String turnUpdate(@PathVariable("id") Integer id, Model model) {
//        this.id = id;
        String sql = "select * from elemensys.shop where id=" + id;
        List<Map<String, Object>> shop_item = jdbcTemplate.queryForList(sql);
        model.addAttribute("shopUpdate", shop_item.get(0));
        return "/shop/shopUpdate";
    }

    //    跳转到添加食品页面
    @GetMapping("/shopAddGoods/{id}")
    public String turnAddGoods(@PathVariable("id") Integer id, Model model) {//获取店铺id

        var sql = "select * from elemensys.shop_food_classify where restaurant_id=" + id;
        List<Map<String, Object>> classifyFoods = jdbcTemplate.queryForList(sql);
        model.addAttribute("classifyFoods", classifyFoods);
        model.addAttribute("restaurant_id", id);
        return "/shop/addGoods";
    }

    //创建食品
    @PostMapping("/createShopAddGoods")
    public String createFoods(HttpServletRequest request) {
        String name = request.getParameter("name");//食品名称
        String introduce = request.getParameter("introduce");//食品描述
        String price = request.getParameter("price");//
        String restaurant_id = request.getParameter("restaurant_id");//店铺id
        String category_id = request.getParameter("category_id");//店铺左侧菜单分类
        System.out.println("创建食品name==>" + name);
        System.out.println("创建食品introduce==>" + introduce);
        System.out.println("创建食品price==>" + price);
        System.out.println("创建食品restaurant_id==>" + restaurant_id);
        System.out.println("创建食品category_id==>" + category_id);
//introduce
        String sql = "insert into elemensys.food(name,introduce,price,restaurant_id,category_id) " +
                "values ('" + name + "','" + introduce + "','" + price + "','" + restaurant_id + "','" + category_id + "')";
        System.out.println("创建食品==>" + sql);
        jdbcTemplate.update(sql);

        return "redirect:/shopList";
    }

    //更新页面 更新数据
    @PostMapping("/dataUpdate")
    public String dataUpdate(HttpServletRequest request) {
        String id = request.getParameter("id");//获取表单提交的数据
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String introduce = request.getParameter("introduce");
        String sql = "update elemensys.shop set name='" + name + "',address='" + address + "',introduce='" + introduce + "' where id=" + id;
        jdbcTemplate.update(sql);
        return "redirect:/shopList";
    }

    //添加店铺
    @PostMapping("/createShop")
    public String createShop(HttpServletRequest request) {
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String introduce = request.getParameter("introduce");
        String phone = request.getParameter("phone");
        String category = request.getParameter("category");
        String start_time = request.getParameter("start_time");
        String end_time = request.getParameter("end_time");
        String sql = "insert into elemensys.shop(name,address,introduce,phone,category,start_time,end_time)" +
                "values ('" + name + "','" + address + "','" + introduce + "','" + phone + "','" + category + "','" + start_time + "','" + end_time + "')";
        System.out.println("添加店铺sql==>"+sql);
        jdbcTemplate.update(sql);
        return "redirect:/shopList";
    }

}
