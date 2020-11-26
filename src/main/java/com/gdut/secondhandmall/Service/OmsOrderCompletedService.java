package com.gdut.secondhandmall.Service;




import com.gdut.secondhandmall.Do.OmsOrderCompletedDO;
import com.gdut.secondhandmall.Dto.PageParamDTO;

import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/17-10:53
 * @description
 **/
public interface OmsOrderCompletedService {
    List<OmsOrderCompletedDO> getAllOrder(PageParamDTO pageParam);

    List<OmsOrderCompletedDO> getOrderForBuyer(PageParamDTO pageParam, String openId);

    List<OmsOrderCompletedDO> getOrderForSeller(PageParamDTO pageParam, String openId);

    boolean confirmCompletion(long orderId);
}
