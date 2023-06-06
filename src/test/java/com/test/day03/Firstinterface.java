package com.test.day03;

import io.restassured.response.Response;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class Firstinterface {
    public static void main(String[] args) {
//        REST-assured
//        Response res = given().
//                header("Content-Type","application/json; charset=UTF-8; charset=UTF-8").
//                header("Authorization","bearer7817044f-a55c-440a-b2ef-7d473fa83753").
//                body("[]").
//        when().
//            post("http://mall.lemonban.com:8107/p/shopCart/info").
//
//        then().
//            log().all().extract().response();
//        //获取相应状态码
//        System.out.println(res.statusCode());
//       //获取运行时间
//        System.out.println(res.time());
//        //获取响应头字段
//        System.out.println(res.header("Set-Cookie"));
//
//        //响应数据提取
//        //提起json数据
//        String result = res.jsonPath().get("shopName[-1]");
//        System.out.println(result);
//
//
//
//        Response res = given().
//                when().
//                    get("http://mall.lemonban.com:8107/search/searchProdPage?prodName=&categoryId=&sort=0&orderBy=0&current=1&isAllProdType=true&st=0&size=12").
//                then().
//                    log().all().extract().response();
//        //条件筛选
//        String result3 = res.jsonPath().get("records.find{it.prodId == 16925}.prodName");
//        System.out.println(result3);
//        //条件筛选全部的
//        ArrayList<String> result4 = res.jsonPath().get("records.findAll{it.price = 0.01}.prodName");//为什么这个位置仅需用==出来的是数组没有数据，而用=出来的数组就有数据？
//        System.out.println(result4);

        //html响应数据提取解析
        Response res = given().
                when().
                    get("https://www.baidu.com").
                then().
                    log().all().extract().response();
        //获取html标签的文本值
        String result5 = res.htmlPath().get("html.head.title");
        System.out.println(result5);
        //获取html标签的属性值
        String result6 = res.htmlPath().get("html.head.meta[2].@content");
        System.out.println(result6);
    }
}
