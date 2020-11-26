package com.gdut.secondhandmall.Bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/12-16:46
 * @description 存进ES中的数据模型
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEssentialsBO {
    private long productId;
    private String productName;
    private String description;
    private short secondaryDirectory;
    private long createTime;
    private double price;
    private String picUrl;
}