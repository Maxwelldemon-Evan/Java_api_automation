package com.test.day04;

import com.alibaba.excel.EasyExcel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ExcelDome {

    public void assertResponse(CaseData caseData, Response res) {
        //从excel中获取到预期结果
        String expected = caseData.getExpected();
        //把json格式的字符串转化为Java的map集合
        HashMap<String, Object> hashMap = RequestDome.jsonStr2Mapper(expected);
        System.out.println(hashMap);
        //循环遍历hasmap
        Set<String> keys = hashMap.keySet();
        for (String key : keys) {
            Object expectedValue = hashMap.get(key);
            /*
            1、statuscode代表http响应状态码断言
            2、bodystr代表响应体字符串断言
            3、gpath表达式代表响应体体字段断言
            */
            if (key.equals("statuscode")) {
                System.out.println("响应状态码断言：期望值：" + expectedValue + ",实际值：" + res.statusCode());
                Assert.assertEquals(res.statusCode(), expectedValue);
            } else if (key.equals("bodystr")) {
                System.out.println("响应体断言：期望值：" + expectedValue + ",实际值：" + res.statusCode());
                Assert.assertEquals(res.body().asString(), expectedValue);
            } else {
                Object actualValue = res.jsonPath().get(key);
                System.out.println("响应字段断言：期望值：" + expectedValue + ",实际值：" + actualValue);
                Assert.assertEquals(expectedValue, actualValue);


            }
        }

    }


    @Test(dataProvider = "getDatas")
    public void test_login(CaseData caseData){

        Response res =RequestDome.request(caseData.getMethod(), caseData.getUrl(), caseData.getHeader(), caseData.getParams());
        //断言
        assertResponse(caseData, res);
    }
//
    @DataProvider
    public Object[] getDatas(){
        //获取到excel的数据
        List<CaseData> datas = EasyExcel.read(new File("src/test/resources/casedata.xlsx")).
                head(CaseData.class).sheet("登录模块").doReadSync();
        return datas.toArray();
    }

}
