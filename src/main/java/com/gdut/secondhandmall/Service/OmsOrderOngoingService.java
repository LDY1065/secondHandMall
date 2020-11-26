package com.gdut.secondhandmall.Service;






import com.gdut.secondhandmall.Do.OmsOrderOngoingDO;
import com.gdut.secondhandmall.Dto.PageParamDTO;

import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/16-20:29
 * @description
 **/
public interface OmsOrderOngoingService {
    List<OmsOrderOngoingDO> getAllUnfinishedOrders(PageParamDTO pageParam);

    List<OmsOrderOngoingDO> getAllUnfinishedOrdersForBuyer(PageParamDTO pageParam, String openId);

    List<OmsOrderOngoingDO> getAllUnfinishedOrdersForseller(PageParamDTO pageParam, String openId);

    boolean postNewOrder(String sessionKey, long productId);
}
