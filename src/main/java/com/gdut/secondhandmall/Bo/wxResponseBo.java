package com.gdut.secondhandmall.Bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class wxResponseBo {
    private String openid;
    private String session_key;
    private String errcode;
    private String errmsg;
}
