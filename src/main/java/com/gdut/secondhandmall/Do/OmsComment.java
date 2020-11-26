package com.gdut.secondhandmall.Do;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class OmsComment {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "订单编号")
    private Long orderId;

    @ApiModelProperty(value = "买家评价")
    private String buyerComment;

    @ApiModelProperty(value = "买家评论附带的图片")
    private String buyerPicUrl;

    @ApiModelProperty(value = "买方评价时间")
    private Date buyerTime;

    @ApiModelProperty(value = "卖家评价")
    private String sellerComment;

    @ApiModelProperty(value = "卖家评论附带的图片")
    private String sellerPicUrl;

    @ApiModelProperty(value = "卖方评价时间")
    private Date sellerTime;
}