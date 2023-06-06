package com.framework.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类：用来读取excel中的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseData {
    private String id;
    private String title;
    private String priority;
    private String method;
    private String url;
    private String header;
    private String params;
    private String expected;
    private String extract;
    private String afterSQL;
}
