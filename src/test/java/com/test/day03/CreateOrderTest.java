package com.test.day03;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CreateOrderTest {
    public static void main(String[] args) {
        //1、登录
        Response res = given().
                    header("Content-Type","application/json;charset=UTF-8").
                    body("{\"principal\":\"lemon_auto\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}").
                when().
                    post("http://mall.lemonban.com:8107/login").
                then().
                    log().all().extract().response();
        String token = "bearer"+res.jsonPath().get("access_token");

        //2、搜索商品
        Response res2 = given().
                when().
                    get("http://mall.lemonban.com:8107/search/searchProdPage?prodName=py56测试&categoryId=&sort=0&orderBy=0&current=1&isAllProdType=true&st=0&size=12").
                then().
                    log().all().extract().response();
//        获取商品的id（prodId）
        int prodId = res2.jsonPath().get("records.find{it.prodName == 'py56测试'}.prodId");

        //3、获取商品的详细信息
        Response res3 = given().
                when().
                    get("http://mall.lemonban.com:8107/prod/prodInfo?prodId="+prodId).
                then().
                    log().all().extract().response();
//        获取商品的id（prodId）
        int skuId = res3.jsonPath().get("skuList[0].skuId");
        //4、添加到购物车
        Response res4 = given().
                    header("Content-Type","application/json; charset=UTF-8").
                    header("Authorization",token).
                    body("{\"basketId\":0,\"count\":1,\"prodId\":"+prodId+",\"shopId\":1,\"skuId\":"+skuId+"}").
                when().
                    post("http://mall.lemonban.com:8107/p/shopCart/changeItem").
                then().
                    log().all().extract().response();

        //5、查询购物车信息接口
        Response res5 = given().
                header("Content-Type","application/json; charset=UTF-8; charset=UTF-8").
                header("Authorization",token).
                body("[]").
        when().
            post("http://mall.lemonban.com:8107/p/shopCart/info").

        then().
            log().all().extract().response();
//        获取basketId(购物车ID)
        int basketId = res5.jsonPath().get("shopCartItemDiscounts[0].shopCartItems[0].basketId[0]");

        //6、点击结算按钮接口(确认订单)
        Response res6 = given().
                header("Content-Type", "application/json; charset=UTF-8").
                header("Authorization", token).
                body("{\"addrId\":0,\"basketIds\":[" + basketId + "],\"couponIds\":[],\"isScorePay\":0," +
                        "\"userChangeCoupon\":0,\"userUseScore\":0,\"uuid\":\"70d3338a-caf2-4205-bbeb-bde5bf85c9df\"}").
                when().
                post("http://mall.lemonban.com:8107/p/order/confirm").
                then().
                log().all().extract().response();

        //7.提交订单
        Response res7 = given().
                header("Content-Type", "application/json; charset=UTF-8").
                header("Authorization", token).
                body("{\"orderShopParam\":[{\"remarks\":\"\",\"shopId\":1}],\"uuid\":\"70d3338a-caf2-4205-bbeb-bde5bf85c9df\"}").
                when().
                post("http://mall.lemonban.com:8107/p/order/submit").
                then().
                log().all().extract().response();
        String orderNumbers = res7.jsonPath().get("orderNumbers");

        //8.取消订单
        Response res8 = given().
                header("Content-Type", "application/json; charset=UTF-8").
                header("Authorization", token).
                when().
                put("http://mall.lemonban.com:8107/p/myOrder/cancel/"+orderNumbers).
                then().
                log().all().extract().response();


    }
}
