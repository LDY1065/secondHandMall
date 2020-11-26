package com.gdut.secondhandmall.Service.impl;



import com.gdut.secondhandmall.Dao.OmsOrderOngoingDao;
import com.gdut.secondhandmall.Dao.OmsOrderReturnApplyDao;
import com.gdut.secondhandmall.Dao.PmsProductOfflineDao;
import com.gdut.secondhandmall.Dao.PmsProductOnlineDao;
import com.gdut.secondhandmall.Do.OmsOrderOngoingDO;
import com.gdut.secondhandmall.Do.OmsOrderReturnApplyDO;
import com.gdut.secondhandmall.Do.PmsProductOfflineDO;
import com.gdut.secondhandmall.Do.PmsProductOnlineDO;
import com.gdut.secondhandmall.Dto.PageParamDTO;
import com.gdut.secondhandmall.Dto.VerificationDTOForOrder;
import com.gdut.secondhandmall.Service.OmsOrderReturnApplyService;
import com.gdut.secondhandmall.Util.ConstantForOrder;
import com.gdut.secondhandmall.Util.TransferUtilForProduct;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/17-11:20
 * @description
 **/
@Service
public class OmsOrderReturnApplyServiceImpl implements OmsOrderReturnApplyService {
    @Autowired
    OmsOrderReturnApplyDao omsOrderReturnApplyDao;
    @Autowired
    OmsOrderOngoingDao orderOngoingDao;
    @Autowired
    PmsProductOfflineDao offlineDao;
    @Autowired
    PmsProductOnlineDao onlineDao;
    @Autowired
    TransferUtilForProduct transferUtilForProduct;

    @Transactional
    @Override
    public boolean putNewApply(long orderId, String reason) {
        OmsOrderOngoingDO orderOngoing = orderOngoingDao.selectByOrderId(orderId);
        if (orderOngoing == null){
            return false;
        }
        if (orderOngoingDao.deleteByOrderId(orderId) != 1){
            return false;
        }
        OmsOrderReturnApplyDO orderReturnApply = new OmsOrderReturnApplyDO();
        orderReturnApply.setOrderId(orderOngoing.getOrderId());
        orderReturnApply.setProductId(orderOngoing.getProductId());
        orderReturnApply.setBuyerId(orderOngoing.getBuyerId());
        orderReturnApply.setSellerId(orderOngoing.getSellerId());
        orderReturnApply.setReason(reason);
        orderReturnApply.setCreateTime(new Date());
        orderReturnApply.setStatus(ConstantForOrder.UNHANDLED);
        omsOrderReturnApplyDao.insert(orderReturnApply);
        return true;
    }

    @Override
    public List<OmsOrderReturnApplyDO> getAll(PageParamDTO pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
        List<OmsOrderReturnApplyDO> orderReturnApplys = omsOrderReturnApplyDao.selectAll();
        return orderReturnApplys;
    }

    @Override
    public List<OmsOrderReturnApplyDO> getOrderForBuyer(PageParamDTO pageParam, String openId) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
        List<OmsOrderReturnApplyDO> orderReturnApplys = omsOrderReturnApplyDao.selectByOpenId(openId);
        return orderReturnApplys;
    }

    @Override
    public boolean completeVerification(VerificationDTOForOrder verification) {
        //如果isPassed为空就返回错误返回值-1
        if (verification.getIsPassed() == null){
            return false;
        }

        long orderId = verification.getOrderId();
        boolean isPassed = verification.getIsPassed().equals("yes");
        System.out.println(isPassed);
        verification.setStatus(ConstantForOrder.HANDLED);
        verification.setHandleTime(new Date());
        OmsOrderReturnApplyDO order = omsOrderReturnApplyDao.selectByOrderId(orderId);
        System.out.println(verification);
        omsOrderReturnApplyDao.updateDetail(verification);
        //如果不通过就留在审核表中
        if (!isPassed) {
            return true;
        }
        //如果通过就将商品从已售商品表中删除，放回在售商品表中
        PmsProductOfflineDO productOffline = offlineDao.selectByProductId(order.getProductId());
        System.out.println(productOffline);
        if (productOffline == null){
            return false;
        }
        if (offlineDao.deleteByPrimaryKey(productOffline.getId()) != 1) {
            return false;
        }
        PmsProductOnlineDO productOnline = transferUtilForProduct.offlineToOnline(productOffline);
        onlineDao.insert(productOnline);
        return true;
    }


}
