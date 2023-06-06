package com.framework.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.entity.CaseData;
import com.framework.util.JDBCUtil;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

/**
 * 所有测试类的基类
 */
public class BaseTest {

    Logger logger = Logger.getLogger(BaseTest.class);

    /**
     * 发起请求的
     * @param caseData
     * @return
     */
    public  Response request(CaseData caseData) {

        //获取接口数据
        String method = caseData.getMethod();
        String header = caseData.getHeader();
        String url = caseData.getUrl();
        String params = caseData.getParams();
        String extract = caseData.getExtract();
        String afterSQL = caseData.getAfterSQL();


        //替换请求头
        header = replaceParam(header);
        //替换请求参数
        params = replaceParam(params);

        //记录日志
        logger.info("-----------------请求数据---------------------");
        logger.info("请求方法:" + method);
        logger.info("请求地址:" + url);
        logger.info("请求头:" + header);
        logger.info("请求参数:" + params);

        //该方法需要能够支持所有的请求方式和传参方式
        //定义一个请求指定类型的对象
        RequestSpecification requestSpecification = given();
        Response res = null;
        if (header != null) {
            //设置请求头
            requestSpecification.headers(jsonStr2Mapper(header));
        }
        //请求方法处理
        if (caseData.getMethod().equalsIgnoreCase("get")) {
            //get请求处理
            if(params == null){
                res = requestSpecification.get(caseData.getUrl()).then().extract().response();
            }else {
                res = requestSpecification.get(url + params).then().extract().response();
            }
        } else if (caseData.getMethod().equalsIgnoreCase("post")) {
            //post请求处理,json传参，form表单传参，文件传参
            if (header.contains("application/json")) {
                //params为json格式
                res = requestSpecification.body(params).post(url).then().extract().response();
            } else if (header.contains("application/x-www-form-urlencoded")) {
                //params为表单格式
                res = requestSpecification.body(params).post(url).then().extract().response();
            } else if (header.contains("multipart/form-data")) {
                //params为文件路径
                res = requestSpecification.multiPart(new File(params)).post(url).then().extract().response();
            }else {
                logger.error("不支持该传参格式");
            }
        } else if (method.equalsIgnoreCase("put")) {
            //put请求处理
            if (header.contains("application/json")) {
                //params为json格式
                res = requestSpecification.body(params).put(url).then().extract().response();
            } else if (header.contains("application/x-www-form-urlencoded")) {
                //params为表单格式
                res = requestSpecification.body(params).put(url).then().extract().response();
            }else {
                logger.error("不支持该传参格式");
            }
        } else if (method.equalsIgnoreCase("delete")) {
            //delete请求的处理
            if(params == null){
                res = requestSpecification.delete(url).then().extract().response();
            }else {
                res = requestSpecification.delete(url + params).then().extract().response();
            }
        }else {
            logger.error("不支持该请求方式");
        }

        //记录接口的响应数据日志
        logger.info("-----------------响应数据---------------------");
        logger.info("http状态码："+res.statusCode());
        logger.info("响应时间："+res.time()+"ms");
        logger.info("响应头："+res.headers().asList());
        logger.info("响应体："+res.body().asString());

        //提取响应的数据
        extractResponse(extract, res);
        //提取数据库中的字段数据
        extractDatabase(afterSQL);

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

    /**
     *提取数据库中的单个字写入到环境变量中
     * @param afterSQL 后置执行sql
     */
    public void extractDatabase(String afterSQL){
        if(afterSQL != null){
            //把json格式的后置sql数据转化为map
            HashMap<String,Object> hashMap = jsonStr2Mapper(afterSQL);
            //遍历hashmap
            Set<String> keys = hashMap.keySet();
            for (String key : keys){
                Object sql = hashMap.get(key);
                Object result = JDBCUtil.querySingleData(sql+"");
                Environment.env.put(key,result);
            }
        }
    }

    /**
     * 提取响应结果中的字段
     * @param extract
     * @param res
     */
    public void extractResponse(String extract, Response res){
        if(extract != null){
            //把json格式的字符串转化为map
            HashMap<String, Object> hashMap = jsonStr2Mapper(extract);
            //遍历map
            Set<String> keys = hashMap.keySet();
            for (String key : keys){
                if (key.contains("bodystr")){
                    //返回体保存到环境变量里面去
                    Environment.env.put(key,res.body().asString());
                }else {
                    //key:字段名 value：GPath表达式
                    String value_gpath = (String)hashMap.get(key);
                    //通过GPath表达式获取到对应的值
                    Object data = res.jsonPath().get(value_gpath);
                    logger.info("提取的字段："+key+":"+data);
                    Environment.env.put(key,data);
                }

            }
        }
    }

    /**
     * 进行变量的替换，将{{xxx}}替换为环境变量xxx对应的值
     * @param str 原始的要替换的字符串
     * @return 替换之后的结果
     */
    public String replaceParam(String str){
        if(str != null){
            //通过正则表达式编译生成匹配对象
            Pattern pattern =Pattern.compile("\\{\\{(.*?)\\}\\}");
            //通过匹配对象得到匹配器，需要传入原始字符串
            Matcher matcher = pattern.matcher(str);
            //循环去找到字符串中所有符合正则表达式的子串
            while(matcher.find()){
                String varName =matcher.group(1);
                Object value = Environment.env.get(varName);
                str = str.replace(matcher.group(), value+"");
            }
        }
        return str;
    }

    /**
     *断言方法的重新封装
     * @param caseData 测试数据
     * @param res   响应数据
     */
    public void assertResponse(CaseData caseData, Response res) {
        //从excel中获取到预期结果
        String expected = caseData.getExpected();
        if (expected != null){
            //把json格式的字符串转化为Java的map集合
            HashMap<String, Object> hashMap = jsonStr2Mapper(expected);
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
                    assertEquals(res.statusCode(), expectedValue,"http状态码");
                } else if (key.equals("bodystr")) {
                    assertEquals(res.body().asString(), expectedValue,"响应体字符串");
                } else {
                    Object actualValue = res.jsonPath().get(key);
                    assertEquals(expectedValue, actualValue,"提取响应字段");
                }
            }
        }

    }

    public void assertEquals(String actualValue, String expectedValue, String tips){
        try{
            Assert.assertEquals(actualValue,expectedValue);
            logger.info("断言【"+tips+"】成功，期望值："+expectedValue+", 实际值："+actualValue);
        }catch (AssertionError e){
            logger.error("断言【"+tips+"】失败，期望值："+expectedValue+", 实际值："+actualValue);
            throw e;
        }
    }

    public void assertEquals(int actualValue, int expectedValue, String tips){
        try{
            Assert.assertEquals(actualValue,expectedValue);
            logger.info("断言【"+tips+"】成功，期望值："+expectedValue+", 实际值："+actualValue);
        }catch (AssertionError e){
            logger.error("断言【"+tips+"】失败，期望值："+expectedValue+", 实际值："+actualValue);
            throw e;
        }
    }

    public void assertEquals(long actualValue, long expectedValue, String tips){
        try{
            Assert.assertEquals(actualValue,expectedValue);
            logger.info("断言【"+tips+"】成功，期望值："+expectedValue+", 实际值："+actualValue);
        }catch (AssertionError e){
            logger.error("断言【"+tips+"】失败，期望值："+expectedValue+", 实际值："+actualValue);
            throw e;
        }
    }

    public void assertEquals(boolean actualValue, boolean expectedValue, String tips){
        try{
            Assert.assertEquals(actualValue,expectedValue);
            logger.info("断言【"+tips+"】成功，期望值："+expectedValue+", 实际值："+actualValue);
        }catch (AssertionError e){
            logger.error("断言【"+tips+"】失败，期望值："+expectedValue+", 实际值："+actualValue);
            throw e;
        }
    }

    public void assertEquals(Object actualValue, Object expectedValue, String tips){
        try{
            Assert.assertEquals(actualValue,expectedValue);
            logger.info("断言【"+tips+"】成功，期望值："+expectedValue+", 实际值："+actualValue);
        }catch (AssertionError e){
            logger.error("断言【"+tips+"】失败，期望值："+expectedValue+", 实际值："+actualValue);
            throw e;
        }
    }
}
