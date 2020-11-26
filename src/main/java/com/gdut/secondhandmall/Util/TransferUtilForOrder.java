package com.gdut.secondhandmall.Util;



import com.gdut.secondhandmall.Do.OmsOrderCompletedDO;
import com.gdut.secondhandmall.Do.OmsOrderOngoingDO;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/19-10:02
 * @description
 **/
@Component
public class TransferUtilForOrder {
    public OmsOrderCompletedDO orderOngoingToOrderCompleted(OmsOrderOngoingDO orderOngoing){
        OmsOrderCompletedDO order = new OmsOrderCompletedDO();
        order.setOrderId(orderOngoing.getOrderId());
        order.setProductId(orderOngoing.getProductId());
        order.setTime(new Date());
        order.setBuyerId(orderOngoing.getBuyerId());
        order.setPayType(new Byte(String.valueOf(orderOngoing.getPayType())));
        order.setSellerId(orderOngoing.getSellerId());
        order.setTotalAmount(orderOngoing.getTotalAmount());
        return order;
    }
}
