package com.moon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @RequestMapping("/orderList")
    public String orderList(Model model){
        String sql ="select * from elemensys.order order by id asc";
        List<Map<String,Object>> order_list =jdbcTemplate.queryForList(sql);
        model.addAttribute("orderList",order_list);
        return "/orderList";
    }
}
