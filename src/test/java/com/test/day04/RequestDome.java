package com.test.day04;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import java.io.File;
import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class RequestDome {
    public static Response request(String method, String url, String header, String params) {
        //该方法需要能够支持所有的请求方式和传参方式
        //定义一个请求指定类型的对象
        RequestSpecification requestSpecification = given();
        Response res = null;
        if (header != null) {
            //设置请求头
            requestSpecification.headers(jsonStr2Mapper(header));
        }
        //请求方法处理
        if (method.equalsIgnoreCase("get")) {
            //get请求处理
            res = requestSpecification.get(url + params).then().log().all().extract().response();
        } else if (method.equalsIgnoreCase("post")) {
            //post请求处理,json传参，form表单传参，文件传参
            if (header.contains("application/json")) {
                //params为json格式
                res = requestSpecification.body(params).post(url).then().log().all().extract().response();
            } else if (header.contains("application/x-www-form-urlencoded")) {
                //params为表单格式
                res = requestSpecification.body(params).post(url).then().log().all().extract().response();
            } else if (header.contains("multipart/form-data")) {
                //params为文件路径
                res = requestSpecification.multiPart(new File(params)).post(url).then().log().all().extract().response();
            }
        } else if (method.equalsIgnoreCase("put")) {
            //put请求处理
        } else if (method.equalsIgnoreCase("delete")) {
            //delete请求的处理
        }
        return res;

    }

    /**
     * json格式的字符串转化为Java中的集合
     * @param str json字符串
     * @return hashmap集合
     */
    public static HashMap<String, Object> jsonStr2Mapper(String str){
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,Object> hashMap;
        try {
            hashMap = objectMapper.readValue(str, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return hashMap;
    }


    public static void main(String[] args) {
//        String str = "{\"Content\":\"application/json\", \"XXX\":\"YYY\"}";
//        System.out.println(jsonStr2Mapper(str));
//        request("post",
//                "http://mall.lemonban.com:8107/login",
//                "{\"Content-Type\":\"application/json;charset=UTF-8\"}",
//                "{\"principal\":\"lemon_auto\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}"
//                );
        request("post",
                "http://erp.lemfix.com/user/login",
                "{\"Content-Type\":\"application/x-www-form-urlencoded;charset=UTF-8\"}",
                "loginame=admin&password=e10adc3949ba59abbe56e057f20f883e"
        );


//        request("post",
//                "http://mall.lemonban.com:8107/p/file/upload",
//                "{\"Content-Type\":\"multipart/form-data\",\"Authorization\":\"bearer9f851758-ba2d-4edf-b385-820d1aadec93\"}",
//                "C:\\Users\\a\\Desktop\\test.jpg"
//        );
//        Response res = request("Get",
//                "http://mall.lemonban.com:8107/search/searchProdPage",
//                null,
//                "?prodName=py56测试");
//        System.out.println(res.statusCode());
//        System.out.println(res.time());
//        String result = res.jsonPath().get("records[0].prodName");
//        System.out.println(result);


    }
}
