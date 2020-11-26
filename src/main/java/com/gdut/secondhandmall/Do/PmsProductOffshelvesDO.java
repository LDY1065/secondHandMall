package com.gdut.secondhandmall.Do;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
* @author GGXian
* @project secondhandmall
* @createTIme 2020/08/11
* @description 
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductOffshelvesDO {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private String openid;

    @ApiModelProperty(value = "商品id")
    private Long productId;

    @ApiModelProperty(value = "下架原因")
    private String reason;

    @ApiModelProperty(value = "审核人")
    private Long verifyMan;

    @ApiModelProperty(value = "下架时间")
    private Date time;
}