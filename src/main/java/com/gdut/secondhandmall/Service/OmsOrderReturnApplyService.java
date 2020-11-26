package com.gdut.secondhandmall.Service;






import com.gdut.secondhandmall.Do.OmsOrderReturnApplyDO;
import com.gdut.secondhandmall.Dto.PageParamDTO;
import com.gdut.secondhandmall.Dto.VerificationDTOForOrder;

import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/17-11:17
 * @description
 **/
public interface OmsOrderReturnApplyService {
    boolean putNewApply(long orderId, String reason);

    List<OmsOrderReturnApplyDO> getAll(PageParamDTO pageParam);

    List<OmsOrderReturnApplyDO> getOrderForBuyer(PageParamDTO pageParam, String openId);

    boolean completeVerification(VerificationDTOForOrder verification);
}
