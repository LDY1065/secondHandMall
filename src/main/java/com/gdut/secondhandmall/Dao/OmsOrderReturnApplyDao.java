package com.gdut.secondhandmall.Dao;



import com.gdut.secondhandmall.Do.OmsOrderReturnApplyDO;
import com.gdut.secondhandmall.Dto.VerificationDTOForOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author GGXian
* @project secondhandmall
* @createTIme 2020/08/05
* @description 
*/
@Mapper
public interface OmsOrderReturnApplyDao {
    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderReturnApplyDO record);

    OmsOrderReturnApplyDO selectByPrimaryKey(Long id);

    List<OmsOrderReturnApplyDO> selectAll();

    int updateByPrimaryKey(OmsOrderReturnApplyDO record);

    List<OmsOrderReturnApplyDO> selectByOpenId(String openId);

    int updateDetail(VerificationDTOForOrder verification);

    OmsOrderReturnApplyDO selectByOrderId(long orderId);
}