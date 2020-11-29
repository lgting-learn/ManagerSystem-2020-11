package com.moon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    //页面跳转
//    @RequestMapping("/userList")
//    public String userList(Model model) {
//        return "/userList";//本地文件夹路径
//    }
    //查询数据库 用户表所有信息
    @RequestMapping("/userList")
    public String userList(Model model) {
        String sql = "select * from elemensys.user order by id asc";
        List<Map<String, Object>> list_user = jdbcTemplate.queryForList(sql);
        System.out.println("list_user==>" + list_user);
        model.addAttribute("userList", list_user);
        return "/userList";//获取数据 再指定页面
    }
}
