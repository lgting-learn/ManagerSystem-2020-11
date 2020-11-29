package com.moon.controller;

import org.attoparser.dom.INode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class FoodController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping("/foodList")
    public String foodList(Model model) {
        String sql = "select * from elemensys.food order by id asc";
        List<Map<String, Object>> food_list = jdbcTemplate.queryForList(sql);
        model.addAttribute("foodList", food_list);
        return "/food/foodList";
    }

    @GetMapping("/foodDelete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        String sql = "delete from elemensys.food where id=" + id;
        jdbcTemplate.update(sql);
        return "redirect:/foodList";
    }

    //跳转到更新页面
    @GetMapping("/foodUpdate/{id}")
    public String turnUpdate(@PathVariable("id") Integer id, Model model) {
//        获取记录 数据回显在编辑页面
        String sql = "select * from elemensys.food where id=" + id;
        List<Map<String, Object>> food_list = jdbcTemplate.queryForList(sql);
        model.addAttribute("foodUpdate", food_list.get(0));
        return "/food/foodUpdate";
    }

    //更新数据
    @PostMapping("/foodDataUpdate")
    public String update(HttpServletRequest request) {
        String id = request.getParameter("id");//获取表单提交的数据
        String name = request.getParameter("name");
        String introduce = request.getParameter("introduce");
        String score = request.getParameter("score");
        String sql = "update elemensys.food set name='" + name + "',introduce='" + introduce + "',score='" + score + "' where id=" + id;
        jdbcTemplate.update(sql);
        return "redirect:/foodList";
    }
}
