package com.gdut.secondhandmall.Util;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/11-15:28
 * @description
 **/
@Component
public class TimeUtil {
    private static final String PATTERN_FOR_MYSQL = "yyyy-MM-dd HH:mm:ss";
    private static final String PATTERN_FOR_ES = "yyyy-MM-dd";
    private static ThreadLocal<DateFormat> mysqlThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(PATTERN_FOR_MYSQL);
        }
    };

    private static ThreadLocal<DateFormat> EsThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(PATTERN_FOR_ES);
        }
    };

    public static synchronized String getTimeForEs(){
        return EsThreadLocal.get().format(new Date()).replace("-", "");
    }

    public static synchronized String getTimeForEs(Date date){
        return EsThreadLocal.get().format(date).replace("-", "");
    }

    public static synchronized Date getTimeForEs(String date) throws ParseException {
        return EsThreadLocal.get().parse(date);
    }

    public static synchronized Date getTimeForMysql(String date) throws ParseException {
        return mysqlThreadLocal.get().parse(date);
    }
}
