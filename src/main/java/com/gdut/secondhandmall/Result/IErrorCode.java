package com.gdut.secondhandmall.Result;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTIme 2020/8/3-14:06
 * @description 封装API的错误码
 **/
public interface IErrorCode {
    long getCode();

    String getMessage();
}
