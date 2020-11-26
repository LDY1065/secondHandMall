package com.gdut.secondhandmall.Bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class userBoughtBo {
    private String tradeName;
    private String pictureUrl;
    private String price;
    private String time;
}
