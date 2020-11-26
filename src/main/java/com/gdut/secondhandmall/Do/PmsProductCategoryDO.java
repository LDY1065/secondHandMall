package com.gdut.secondhandmall.Do;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @author GGXian
* @project secondhandmall
* @createTIme 2020/08/05
* @description 
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductCategoryDO {
    @ApiModelProperty(value = "主键")
    private Short id;

    @ApiModelProperty(value = "商品一级目录")
    private String primaryDirectory;

    @ApiModelProperty(value = "商品二级目录")
    private String secondaryDirectory;
}