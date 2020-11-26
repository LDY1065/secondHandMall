package com.gdut.secondhandmall.Do;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
* @author GGXian
* @project secondhandmall
* @createTIme 2020/08/05
* @description 
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductOfflineDO {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private String openid;

    @ApiModelProperty(value = "商品id")
    private Long productId;

    @ApiModelProperty(value = "商品名")
    private String productName;

    @ApiModelProperty(value = "商品目录id")
    private Short directoryId;

    @ApiModelProperty(value = "商品图片url")
    private String picUrl;

    @ApiModelProperty(value = "描述")
    private String description;

    private BigDecimal originalPrice;

    @ApiModelProperty(value = "成交价格")
    private BigDecimal price;

    @ApiModelProperty(value = "发布时间")
    private Date publishTime;

    private Short visitor;

    @ApiModelProperty(value = "成交时间")
    private Date dealTime;
}