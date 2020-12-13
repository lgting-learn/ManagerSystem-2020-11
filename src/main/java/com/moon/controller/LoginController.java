package com.moon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @RequestMapping("/")
    public String index() {
        return "/index";//正式
//      return "/shop/addShop";//测试
    }

//登陆页面
//    @RequestMapping("/user/login")
//    public String login(
//            @RequestParam("username") String username,
//            @RequestParam("password") String password,
//            HttpSession session,//拦截器
//            Model model
//    ) {
//        if (!StringUtils.isEmpty(username) && "1".equals(password)) {
//            session.setAttribute("loginUser", username);
//            return "redirect:/";
//        } else {
//            model.addAllAttributes("msg", "用户名或密码错误");
//            return "index";
//        }
//    }

}
