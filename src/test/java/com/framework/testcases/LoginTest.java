package com.framework.testcases;

import com.alibaba.excel.EasyExcel;
import com.framework.entity.CaseData;

import com.framework.common.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class LoginTest extends BaseTest {
    @Test(dataProvider = "getDatas")
    public void test_login(CaseData caseData){

        Response res = request(caseData);
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
