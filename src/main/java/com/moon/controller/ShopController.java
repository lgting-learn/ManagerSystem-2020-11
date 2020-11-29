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
    Integer id = 0;

    @RequestMapping("/shopList")
    public String shopList(Model model) {
        String sql = "select * from elemensys.shop order by id asc";
        List<Map<String, Object>> shop_list = jdbcTemplate.queryForList(sql);
        model.addAttribute("shopList", shop_list);
        return "/shop/shopList";
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

    //更新页面 更新数据
    @PostMapping("/dataUpdate")
    public String dataUpdate(HttpServletRequest request) {
        String id = request.getParameter("id");//获取表单提交的数据
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String introduce = request.getParameter("introduce");
        String sql = "update elemensys.shop set name='"+name+"',address='"+address+"',introduce='"+introduce+"' where id=" + id;
        jdbcTemplate.update(sql);
        return "redirect:/shopList";
    }

}
