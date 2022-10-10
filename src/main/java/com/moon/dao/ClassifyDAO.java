package com.moon.dao;

import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ClassifyDAO {
    @Autowired
    JdbcTemplate jdbcTemplate;

    //后台管理系统
    //后台管理系统-用户列表
    @PostMapping("/api/userListManager")
    public Map<String, Object> userListManager(HttpServletRequest request) {
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        String sql = "select * from elemensys.user_register " + " limit " + start + "," + limit + "";

        String sql_num = "select count(*) as total from elemensys.user_register";
        List<Map<String, Object>> food_list_manager = jdbcTemplate.queryForList(sql);
        String num_result = jdbcTemplate.queryForList(sql_num).get(0).get("total").toString();
        Map<String, Object> finallyObj = new HashMap<String, Object>();
        finallyObj.put("datas", food_list_manager);
        finallyObj.put("total", num_result);
        return finallyObj;
    }

    //后台管理系统-商家列表
    @PostMapping("/api/shopListManager")
    public Map<String, Object> shopListManager(HttpServletRequest request) {
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        String id = request.getParameter("id");
        String sql = "";
        Map<String, Object> finallyObj = new HashMap<String, Object>();
        //获取分类
        String classify = "select distinct(classify_id) as value,title from elemensys.classify order by cast(classify_id as decimal) asc limit 1000";

        List<Map<String, Object>> classify_list = jdbcTemplate.queryForList(classify);
        List<Map<String, Object>> food_list_manager = null;
        if (id.isBlank()) {//商家表格列表
            sql = "select id,name,address,introduce,image_path,phone,category as value,start_time,end_time,score,per_capita,month_sales from elemensys.shop order by id desc" + " limit " + start + "," + limit + "";
            //获取条数
            String sql_num = "select count(*) as total from elemensys.shop";
            String num_result = jdbcTemplate.queryForList(sql_num).get(0).get("total").toString();
            finallyObj.put("total", num_result);
            food_list_manager = jdbcTemplate.queryForList(sql);
            food_list_manager = shopCategoryTran(food_list_manager, classify_list);
        } else {//通过id获取商家详情与分类
            sql = "select id,name,address,introduce,image_path,phone,category as value,start_time,end_time,score,per_capita,month_sales from elemensys.shop where id='" + id + "' limit 1";
            food_list_manager = jdbcTemplate.queryForList(sql);
            //为了下拉框回显 获取分类:去重、数字字符串转成数字升序排序
            String sqlCategory = "select distinct(category) as value from elemensys.shop order by cast(category as decimal) asc limit 1000";

            List<Map<String, Object>> food_list_manager_category = jdbcTemplate.queryForList(sqlCategory);
            food_list_manager_category = shopCategoryTran(food_list_manager_category, classify_list);
            finallyObj.put("category", food_list_manager_category);
        }
        finallyObj.put("datas", food_list_manager);
        return finallyObj;
    }

    //后台管理系统-商家列表-编辑按钮 todo:店铺图片未更新
    @PostMapping("/api/shopSingleUpdateManager")
    public Map<String, Object> shopSingleUpdateManager(HttpServletRequest request) {
        String category = request.getParameter("category");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String introduce = request.getParameter("introduce");
        String phone = request.getParameter("phone");
        String id = request.getParameter("id");
        String image_path = request.getParameter("image_path");
        String sql = "update elemensys.shop set " + "category='" + category + "'," + "name='" + name + "'," + "address='" + address + "'," + "introduce='" + introduce + "'," + "phone='" + phone + "'," + "image_path='" + image_path + "'" + " where id='" + id + "'";
        System.out.println("sql 1016==" + sql);
        jdbcTemplate.update(sql);
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("result", "true");
        obj.put("msg", "update success");
        return obj;
    }

    //后台管理系统-商家列表-删除按钮
    @PostMapping("/api/deleteShopManager")
    public Map<String, Object> deleteShopManager(HttpServletRequest request) {
        String id = request.getParameter("id");
        String sql = "delete from  elemensys.shop where id='" + id + "'";
        Map<String, Object> obj = new HashMap<>();
        jdbcTemplate.update(sql);
        obj.put("result", true);
        obj.put("msg", "店铺删除成功");
        return obj;
    }

    //后台管理系统-添加店铺 todo:店铺图片未更新
    @PostMapping("/api/addShopManager")
    public Map<String, Object> addShopManager(HttpServletRequest request) {
        String name = request.getParameter("name"); //店铺名称
        String address = request.getParameter("address"); //店铺地址
        String phone = request.getParameter("phone");//电话
        String introduce = request.getParameter("introduce");//店铺介绍
        String category = request.getParameter("category");//分类
        String service = request.getParameter("service"); //店铺特点 todo
        String delivery_cost = request.getParameter("delivery_cost"); //配送费 todo
        String initiate_price = request.getParameter("initiate_price"); //起步价 todo
        String start_time = request.getParameter("start_time");//营业开始时间
        String end_time = request.getParameter("end_time");//营业结束时间
        String image_path = request.getParameter("image_path"); //店铺封面
        String discount = request.getParameter("discount"); //优惠 todo
        long id = new Date().getTime(); //优惠 todo
        Map<String, Object> obj = new HashMap<>();
        // 时间戳作为id，多人操作是否会出现id重复
        String sql = "insert into elemensys.shop(id,name,address,phone,introduce,category,start_time,end_time,image_path) " + "values ('" + id + "','" + name + "','" + address + "','" + phone + "','" + introduce + "','" + category + "','" + start_time + "','" + end_time + "','" + image_path + "')";
        jdbcTemplate.update(sql);
        obj.put("result", true);
        obj.put("msg", "店铺添加成功");
        return obj;
    }


    //后台管理系统-食品列表
    @PostMapping("/api/foodListManager")
    public Map<String, Object> foodListManager(HttpServletRequest request) {
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        String id = request.getParameter("id");
        String sql = "";
        Map<String, Object> finallyObj = new HashMap<String, Object>();
        //获取分类
//        String classify = "select distinct(classify_id) as value,title from elemensys.classify order by cast(classify_id as decimal) asc limit 1000";
//
//        List<Map<String, Object>> classify_list = jdbcTemplate.queryForList(classify);
        List<Map<String, Object>> food_list_manager = null;

        if (id.isBlank()) {//食品表格列表
            sql = "select * from elemensys.food " + " limit " + start + "," + limit + "";
            ;
            //获取条数
            String sql_num = "select count(*) as total from elemensys.food";
            String num_result = jdbcTemplate.queryForList(sql_num).get(0).get("total").toString();

            finallyObj.put("total", num_result);
            food_list_manager = jdbcTemplate.queryForList(sql);
//            food_list_manager = shopCategoryTran(food_list_manager, classify_list);
        } else {//通过id获取商家详情与分类
            sql = "select * from elemensys.food where id='" + id + "' limit 1";
            food_list_manager = jdbcTemplate.queryForList(sql);
            //为了下拉框回显 获取分类:去重、数字字符串转成数字升序排序
//            String sqlCategory = "select distinct(category) as value from elemensys.shop order by cast(category as decimal) asc limit 1000";
//
//            List<Map<String, Object>> food_list_manager_category = jdbcTemplate.queryForList(sqlCategory);
//            food_list_manager_category = shopCategoryTran(food_list_manager_category, classify_list);
//            finallyObj.put("category", food_list_manager_category);
        }
        finallyObj.put("datas", food_list_manager);
        return finallyObj;
    }

    //后台管理系统-食品列表-编辑按钮 todo:分类与食品图片未更新
    @PostMapping("/api/foodSingleUpdateManager")
    public Map<String, Object> foodSingleUpdateManager(HttpServletRequest request) {
        String name = request.getParameter("name");
        String introduce = request.getParameter("introduce");
        String price = request.getParameter("price");
        String category = request.getParameter("category");
        String id = request.getParameter("id");
        String image_path = request.getParameter("image_path");
        String sql = "update elemensys.food set " +
//                "category_id='" + category + "'," +
                "name='" + name + "'," + "introduce='" + introduce + "'," + "price='" + price + "'," + "image_path='" + image_path + "'" + " where id='" + id + "'";
        jdbcTemplate.update(sql);
        System.out.println("0925===" + sql);
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("result", "true");
        obj.put("msg", "update success");
        return obj;
    }

    //后台管理系统-添加店铺 todo:店铺图片未更新
    @PostMapping("/api/addFoodManager")
    public Map<String, Object> addFoodManager(HttpServletRequest request) {
        String name = request.getParameter("name"); //店铺名称
        String introduce = request.getParameter("introduce");//店铺介绍
        String image_path = request.getParameter("image_path"); //店铺封面
        String price = request.getParameter("price"); //店铺封面
        String category_id = request.getParameter("category");//分类 todo
        String special = request.getParameter("special");//特点 todo
        String sku = request.getParameter("sku");//规格 todo
        String packing_extra = request.getParameter("packing_extra");//包装费 todo
        long id = new Date().getTime(); //优惠 todo
        Map<String, Object> obj = new HashMap<>();
        // 时间戳作为id，多人操作是否会出现id重复
        String sql = "insert into elemensys.food(id,name,introduce,image_path,price) " + "values ('" + id + "','" + name + "','" + introduce + "','" + image_path + "','" + price + "')";
        System.out.println("addFoodManager===" + sql);
        jdbcTemplate.update(sql);
        System.out.println("食品添加成功" + sql);
        obj.put("result", true);
        obj.put("msg", "食品添加成功");
        return obj;
    }

    //后台管理系统-食品列表-删除按钮
    @PostMapping("/api/deleteFoodManager")
    public Map<String, Object> deleteFoodManager(HttpServletRequest request) {
        String id = request.getParameter("id");
        String sql = "delete from  elemensys.food where id='" + id + "'";
        Map<String, Object> obj = new HashMap<>();
        jdbcTemplate.update(sql);
        obj.put("result", true);
        obj.put("msg", "食品删除成功");
        return obj;
    }

    //后台管理系统-订单列表
    @PostMapping("/api/orderListManager")
    public Map<String, Object> orderistManager(HttpServletRequest request) {
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
//        SELECT * FROM table WHERE 查询条件 ORDER BY 排序条件 LIMIT ((页码-1)*页大小),页大小;
        String sql = "select * from elemensys.order " + " limit " + start + "," + limit + "";
        String sql_num = "select count(*) as total from elemensys.order";

        List<Map<String, Object>> food_list_manager = jdbcTemplate.queryForList(sql);
        String num_result = jdbcTemplate.queryForList(sql_num).get(0).get("total").toString();
        Map<String, Object> finallyObj = new HashMap<String, Object>();
        finallyObj.put("datas", food_list_manager);
        finallyObj.put("total", num_result);
        return finallyObj;
    }

    //后台管理系统-分类列表
    @PostMapping("/api/classifyListManager")
    public Map<String, Object> classifyListManager(HttpServletRequest request) {
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        String id = request.getParameter("id");
        String sql = "";
        Map<String, Object> finallyObj = new HashMap<String, Object>();
        //获取分类
        String classify = "select distinct(classify_id) as value,title from elemensys.classify order by cast(classify_id as decimal) asc limit 1000";
        List<Map<String, Object>> classify_list = jdbcTemplate.queryForList(classify);
        List<Map<String, Object>> food_list_manager = null;
        if (id.isBlank()) {//商家表格列表
            sql = "select description,image_url,title,classify_id as value from elemensys.classify " + " limit " + start + "," + limit + "";
            //获取条数
            String sql_num = "select count(*) as total from elemensys.classify";
            String num_result = jdbcTemplate.queryForList(sql_num).get(0).get("total").toString();
            finallyObj.put("total", num_result);
            food_list_manager = jdbcTemplate.queryForList(sql);
            food_list_manager = shopCategoryTran(food_list_manager, classify_list);
        } else {//通过id获取商家详情与分类
            sql = "select description,image_url,title,classify_id as value from elemensys.classify where classify_id='" + id + "' limit 1";
            food_list_manager = jdbcTemplate.queryForList(sql);
            //为了下拉框回显 获取分类:去重、数字字符串转成数字升序排序
            String sqlCategory = "select distinct(classify_id) as value,title as label from elemensys.classify order by cast(classify_id as decimal) asc limit 1000";

            List<Map<String, Object>> food_list_manager_category = jdbcTemplate.queryForList(sqlCategory);
//            food_list_manager_category = shopCategoryTran(food_list_manager_category, classify_list);
            finallyObj.put("category", food_list_manager_category);
        }
        finallyObj.put("datas", food_list_manager);
        return finallyObj;

    }

    //后台管理系统-分类列表-编辑按钮 todo:分类与食品图片未更新
    @PostMapping("/api/classifySingleUpdateManager")
    public Map<String, Object> classifySingleUpdateManager(HttpServletRequest request) {
        String description = request.getParameter("description");
        String image_url = image_url = request.getParameter("image_url");

        String id = request.getParameter("id");
        String sql = "update elemensys.classify set " + "description='" + description + "', " + "image_url='" + image_url + "' " + " where classify_id='" + id + "'";
        System.out.println("1010====" + sql);
        jdbcTemplate.update(sql);
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("result", "true");
        obj.put("msg", "update success");
        return obj;
    }


    //搜索 查找食品名称
    @PostMapping("/api/searchFood")
    public Object searchFood(HttpServletRequest request) {
        String key = request.getParameter("key");
        String start = request.getParameter("start");
        String limit = request.getParameter("limit");
//        模糊查找api/shop
        String sql = "select * from elemensys.food where name like '%" + key + "%' limit " + start + "," + limit + "";
        String sql_num = "select count(1) as num from elemensys.food where name like '%" + key + "%'";
        String num_result = jdbcTemplate.queryForList(sql_num).get(0).get("num").toString();
        Object obj_sql = jdbcTemplate.queryForList(sql);
        Object total = jdbcTemplate.queryForList(sql_num);
        Map<String, Object> obj = new HashMap<>();
        obj.put("data", obj_sql);
        obj.put("total", num_result);
        return obj;

    }

    //搜索 查找分类名称
    @PostMapping("/api/searchClassify")
    public Map<String, Object> searchClassify(HttpServletRequest request) {
        String key = request.getParameter("key");
        String start = request.getParameter("start");
        String limit = request.getParameter("limit");
//        模糊查找api/shop
        String sql = "select * from elemensys.classify where title like '%" + key + "%' limit " + start + "," + limit + "";
        System.out.println("sql====" + sql);
        String sql_num = "select count(1) as num from elemensys.classify where title like '%" + key + "%'";
        String num_result = jdbcTemplate.queryForList(sql_num).get(0).get("num").toString();
        Object obj_sql = jdbcTemplate.queryForList(sql);
        Object total = jdbcTemplate.queryForList(sql_num);
        Map<String, Object> obj = new HashMap<>();
        obj.put("data", obj_sql);
        obj.put("total", num_result);
        return obj;
    }

    //    公共方法
    //商家类别category翻译器
    public List<Map<String, Object>> shopCategoryTran(List<Map<String, Object>> list, List<Map<String, Object>> classify_list) {
        for (int i = 0; i < list.size(); i++) {//Iterator遍历结果集报错，后采用for循环解决
            list.get(i).put("label", "");
            for (int j = 0; j < classify_list.size(); j++) {
                if (list.get(i).get("value").equals(classify_list.get(j).get("value").toString())) {
                    list.get(i).put("label", classify_list.get(j).get("title"));
                }
            }
        }
        return list;
    }

    //-------------前端项目
    //前端首页-顶部分类
    @GetMapping("/api/classify")
    public List<Map<String, Object>> apiFood() {
        String sql = "select * from elemensys.classify";
        List<Map<String, Object>> classify_list = jdbcTemplate.queryForList(sql);
        return classify_list;
    }

    //前端首页-所有店铺
    @PostMapping("/api/shop")
    public List<Map<String, Object>> apiShop(HttpServletRequest request) {
        //字符串强转整数Integer.valueOf()
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        String category = "";
        if (request.getParameter("category") != null) {//判断读取参数是否为空
            category = request.getParameter("category");//首页食品分类进入food页面，需要根据种类筛选店铺
        }
        String sql = "";
        if (category.isBlank()) {
            sql = "select * from elemensys.shop limit " + start + "," + limit + "";
        } else {
            sql = "select * from elemensys.shop where category=" + category + " limit " + start + "," + limit + "";
        }
        List<Map<String, Object>> shop_list = jdbcTemplate.queryForList(sql);
        return shop_list;
    }

    //单间店铺详情
    @PostMapping("/api/shopSingle")
    public Object shopSingle(HttpServletRequest request) {
        String id = request.getParameter("id");
        String sql = "select * from elemensys.shop where id=" + id;
        Object obj = jdbcTemplate.queryForList(sql).get(0);
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
            //左侧shop_food_classify的id是food的category_id
            sql = "select * from elemensys.food where category_id=" + id;
//将另一个数据库结果集赋值给另一个结果集的某一属性
            resultsFood = jdbcTemplate.queryForList(sql);
            if (resultsFood.size() != 0) {
                results.get(i).put("foods", resultsFood);
            }
        }

        return results;
    }

    //店铺-右侧 改变食品数量
    @PostMapping("/api/changeFoodsNum")
    public List<Map<String, Object>> changeFoodsNum(HttpServletRequest request) {
        String foodId = request.getParameter("id");
        String restaurantId = request.getParameter("restaurant_id");
        String buy_number = request.getParameter("buy_number");
        String sql = "";
//      isEmpty仅仅是判断空和长度为0字符串
//isBlank判断的是空，长度为0，空白字符（包括空格，制表符\t，换行符\n，换页符\f，回车\r）组成的字符串。
        if (!foodId.isBlank()) {
            sql = "update elemensys.food set buy_number='" + buy_number + "' where id=" + foodId;
            jdbcTemplate.update(sql);
        }
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

    //    食品表购买数量buy_number > 0 则置空
    @PostMapping("/api/batchUpdateFoods")
    public Map<String, Object> batchUpdateFoods(HttpServletRequest request) {
        String restaurant_id = request.getParameter("restaurant_id");
        String sql = "update elemensys.food set buy_number=0 where buy_number>0 and restaurant_id=" + restaurant_id;
        jdbcTemplate.update(sql);
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("result", "true");
        return obj;
    }

    //获取首页顶部-分类详情
    @GetMapping("/api/category")
    public List<Map<String, Object>> category() {
        String sql = "select * from elemensys.category";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        Object ids = "";
        Map<String, Object> obj = new HashMap<>();
        List<Map<String, Object>> sub_results = null;
        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                ids = results.get(i).get("id");
                sql = "select * from elemensys.sub_categories where sub_id=" + ids;
                sub_results = jdbcTemplate.queryForList(sql);
                results.get(i).put("sub_categories", sub_results);
            }
        }
        return results;
    }

    //用户登录/注册
    @PostMapping("/api/login")
    public Map<String, Object> login(HttpServletRequest request) {
        //        该用户是否存在
        Boolean userExist = false;
//        是否能够登录
        Boolean loginFlag = false;

        String name = request.getParameter("name");
        String psw = request.getParameter("psw");
        String type = request.getParameter("type");//登陆还是注册
        String registerTime = request.getParameter("register_time");//注册时间

        String sql = "select * from elemensys.user_register";
        Map<String, Object> obj = new HashMap<>();
        List<Map<String, Object>> results = null;
        if (type.equals("login")) {
            results = jdbcTemplate.queryForList(sql);
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).get("name").equals(name)) {
                    userExist = true;
                    if (results.get(i).get("password").equals(psw)) {
                        loginFlag = true;
                    }
                }
            }
            if (loginFlag) {
                obj.put("result", true);
            } else {
                if (!userExist) {
                    obj.put("result", false);
                    obj.put("msg", "该用户不存在,请先注册");
                    obj.put("errorCode", "404");
                }
                if (!loginFlag) {
                    obj.put("result", false);
                    obj.put("msg", "密码错误");
                    obj.put("errorCode", "404");
                }
            }
        } else if (type.equals("register")) {//用户名必须唯一
            sql = "select count(1) as num from elemensys.user_register";
            String num = jdbcTemplate.queryForList(sql).get(0).get("num").toString();
            if (!num.equals("0")) {
                //新增用户
                sql = "insert into elemensys.user_register(name,password,register_time) values ('" + name + "','" + psw + "','" + registerTime + "')";
                System.out.println("1011 " + sql);
                jdbcTemplate.update(sql);
                obj.put("result", true);
                obj.put("msg", "注册成功");
            } else {
                obj.put("result", false);
                obj.put("msg", "注册失败，该用户已存在");
                obj.put("errorCode", "403");
            }
        }
        return obj;
    }

    //    获取用户信息
    @PostMapping("/api/getUserInfo")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        String name = request.getParameter("name");
        String sql = "select id,name,img_url,sign from elemensys.user_register where name='" + name + "'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result.get(0);
    }

    //获取默认地址
    @PostMapping("/api/getDefaultAddress")
    public Map<String, Object> getDefaultAddress(HttpServletRequest request) {
        String user_id = request.getParameter("user_id");
        String sql = "select * from elemensys.delivery_address where user_register_id='" + user_id + "'";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).get("isDefault").equals("1")) {
                    return results.get(i);
                }
            }
            return results.get(0);
        }
        Map<String, Object> obj = new HashMap<>();
        obj.put("total", 0);
        obj.put("msg", "请新增地址");
        return obj;
    }

    //获取用户地址列表
    @PostMapping("/api/getAddressList")
    public List<Map<String, Object>> getAddressList(HttpServletRequest request) {
        String name = request.getParameter("name");
        String sql = "select id from elemensys.user_register where name='" + name + "'";
        Object id = jdbcTemplate.queryForList(sql).get(0).get("id");
        sql = "select * from elemensys.delivery_address where user_register_id='" + id + "'";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        return results;
    }

    //编辑地址页面 数据回显
    @PostMapping("/api/getAddressSingle")
    public Map<String, Object> getAddressSingle(HttpServletRequest request) {
        String id = request.getParameter("id");
        String sql = "select * from elemensys.delivery_address where id='" + id + "'";
        Map<String, Object> result = new HashMap<>();
        result = jdbcTemplate.queryForList(sql).get(0);
        return result;
    }

    //编辑地址页面
    @PostMapping("/api/editAddress")
    public Map<String, Object> editAddress(HttpServletRequest request) {
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String tel = request.getParameter("tel");
        String isDefault = request.getParameter("isDefault");
        String areaCode = request.getParameter("areaCode");
        String addressDetail = request.getParameter("addressDetail");
        String id = request.getParameter("id");
        String province = request.getParameter("province");
        String county = request.getParameter("county");
        String city = request.getParameter("city");
        String user_register_id = request.getParameter("user_register_id");
        String sql = "";
        List<Map<String, Object>> list = null;
        if (isDefault.equals("1")) {
            sql = "select * from elemensys.delivery_address";
            list = jdbcTemplate.queryForList(sql);
            if (list.size() > 0) {//把
                for (int i = 0; i < list.size(); i++) {
                    sql = "update elemensys.delivery_address set isDefault='0' where id='" + list.get(i).get("id") + "'";
                    jdbcTemplate.update(sql);
                }
            }
        }
        if (type.equals("edit")) {
            sql = "update elemensys.delivery_address set name='" + name + "',phone='" + tel + "',isDefault='" + isDefault + "'," + "province='" + province + "',city='" + county + "',down_town='" + city + "',areaCode='" + areaCode + "',detail_address='" + addressDetail + "' where id='" + id + "'";
        } else {
            sql = "insert into elemensys.delivery_address(name,phone,isDefault,province,city,down_town,areaCode,detail_address,user_register_id)" + "values ('" + name + "','" + tel + "','" + isDefault + "','" + province + "','" + county + "','" + city + "','" + areaCode + "','" + addressDetail + "','" + user_register_id + "')";
        }
        jdbcTemplate.update(sql);
        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        return result;
    }

    //删除地址
    @PostMapping("/api/deleteAddress")
    public Map<String, Object> deleteAddress(HttpServletRequest request) {
        String id = request.getParameter("id");
        String sql = "delete from  elemensys.delivery_address where id='" + id + "'";
        jdbcTemplate.update(sql);
        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        return result;
    }

    //确认订单 提交成功清空该店铺购物车内容
    @PostMapping("/api/comfirmOrder")
    public Map<String, Object> comfirmOrder(HttpServletRequest request) {
//        转换为json数组
        JSONArray jsonArray = JSONArray.fromObject(request.getParameterValues("order_detail"));
        String restaurant_id = request.getParameter("restaurant_id");
        String user_id = request.getParameter("user_id");
        String address_id = request.getParameter("address_id");
        String order_status = request.getParameter("order_status");
        String restaurant_name = request.getParameter("restaurant_name");
        String all_price = request.getParameter("all_price");
        String shop_img = request.getParameter("shop_img");
        String trading_time = request.getParameter("trading_time");
        String sql = "insert into elemensys.order(order_detail,user_id,address_id,restaurant_id," + "order_status,restaurant_name,all_price,shop_img,trading_time) values ('" + jsonArray.getJSONArray(0) + "','" + user_id + "','" + address_id + "','" + restaurant_id + "','" + order_status + "','" + restaurant_name + "','" + all_price + "','" + shop_img + "','" + trading_time + "')";
        jdbcTemplate.update(sql);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "true");
        sql = "update elemensys.food set buy_number=0 where restaurant_id= '" + restaurant_id + "'";
        jdbcTemplate.update(sql);
        return result;
    }

    //获取某用户订单列表
    @PostMapping("/api/getOrderList")
    public List<Map<String, Object>> getOrderList(HttpServletRequest request) {
//        String order_status = request.getParameter("order_status");//订单状态
        String user_id = request.getParameter("user_id");//用户id
//        String sql = "select all_price,order_status,restaurant_name,shop_img,trading_time from elemensys.order where user_id= '" + user_id + "' and order_status='" + order_status + "' order by trading_time desc";
        String sql = "select all_price,order_status,restaurant_name,shop_img,trading_time,id from elemensys.order where user_id= '" + user_id + "' order by trading_time desc";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        return results;
    }

    //获取某用户订单详情
    @PostMapping("/api/getOrderDetail")
    public List<Map<String, Object>> getOderDetail(HttpServletRequest request) {
        String order_id = request.getParameter("order_id");//用户id
        String sql = "select * from elemensys.order where id= '" + order_id + "'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result;
    }

    //获取某店铺评价
    @PostMapping("/api/getShopAppraise")
    public List<Map<String, Object>> getShopAppraise(HttpServletRequest request) {
        String restaurant_id = request.getParameter("restaurant_id");//用户id
        String start = request.getParameter("start");
        String limit = request.getParameter("limit");
        String sql = "select * from elemensys.user_appraise where restaurant_id= '" + restaurant_id + "' order by appraise_time desc limit " + start + "," + limit + "";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result;
    }

    //获取某店铺评分详情
    @PostMapping("/api/getShopRate")
    public List<Map<String, Object>> getShopRate(HttpServletRequest request) {
        String restaurant_id = request.getParameter("restaurant_id");//店铺id
        String sql = "select * from elemensys.restaurant_rate_detail where restaurant_id= '" + restaurant_id + "'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result;
    }

    //用户发表评论
    @PostMapping("/api/setAppraise")
    public Map<String, Object> setAppraise(HttpServletRequest request) {
        String restaurant_id = request.getParameter("restaurant_id");//店铺id
        String user_name = request.getParameter("user_name");//用户名
        String head_img = request.getParameter("head_img");//用户头像
        String rateValue = request.getParameter("rateValue");//用户评分
        String tasteValue = request.getParameter("tasteValue");//口味
        String packValue = request.getParameter("packValue");//包装
        String appraise_time = request.getParameter("appraise_time");//评价时间
        String appraise_detail = request.getParameter("appraise_detail");//评价内容
        JSONArray appraise_img = JSONArray.fromObject(request.getParameterValues("appraise_img"));//评论图片

//        String appraise_img = request.getParameter("appraise_img");
        String sql = "insert into elemensys.user_appraise(user_name,head_img,rate,appraise_time,appraise_detail,appraise_img,restaurant_id) " + "values('" + user_name + "','" + head_img + "','" + rateValue + "','" + appraise_time + "','" + appraise_detail + "','" + appraise_img + "','" + restaurant_id + "')";
        jdbcTemplate.update(sql);
        Map<String, Object> reuslt = new HashMap<>();
        reuslt.put("result", "true");
        return reuslt;
    }

    /**
     * 功能描述:
     * @param: request
     * @Return: java.lang.Object
     * @Author: 86183
     * @Date: 2022-08-18 8:52
     * @deprecated:搜索 查找店铺名称
     */
    @PostMapping("/api/searchShop")
    public Object searchShop(HttpServletRequest request) {
        String key = request.getParameter("key");
        String start = request.getParameter("start");
        String limit = request.getParameter("limit");
//        模糊查找api/shop
        String sql = "select * from elemensys.shop where name like '%" + key + "%' limit " + start + "," + limit + "";
        String sql_num = "select count(1) as num from elemensys.shop where name like '%" + key + "%'";
        System.out.println("sql_num===" + sql_num);

        String num_result = jdbcTemplate.queryForList(sql_num).get(0).get("num").toString();
        System.out.println("num_result===" + num_result);
        Object obj_sql = jdbcTemplate.queryForList(sql);
        Object total = jdbcTemplate.queryForList(sql_num);
        Map<String, Object> obj = new HashMap<>();
        obj.put("data", obj_sql);
        obj.put("total", num_result);
        return obj;
    }


}
