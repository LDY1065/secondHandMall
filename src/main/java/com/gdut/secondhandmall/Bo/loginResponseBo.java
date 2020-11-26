package com.gdut.secondhandmall.Bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class loginResponseBo {
    private String sessionKey;
    private String errorCode;
    private String errorMsg;
}
