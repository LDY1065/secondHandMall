package com.gdut.secondhandmall.Service.impl;



import com.gdut.secondhandmall.Dao.PmsProductOnlineDao;
import com.gdut.secondhandmall.Do.PmsProductOnlineDO;
import com.gdut.secondhandmall.Dto.PageParamDTO;
import com.gdut.secondhandmall.Dto.ProductEssentialsDTO;
import com.gdut.secondhandmall.Service.PmsProductOnlineService;
import com.gdut.secondhandmall.Util.TransferUtilForProduct;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/10-11:50
 * @description
 **/
@Service
public class PmsProductOnlineServiceImpl implements PmsProductOnlineService {
    @Autowired
    PmsProductOnlineDao onlineDao;
    @Autowired
    RedisServiceImpl updateVisitorService;
    @Autowired
    TransferUtilForProduct transferUtilForProduct;

    @Override
    public List<ProductEssentialsDTO> selectessentials() {
        return onlineDao.selectessentials();
    }

    @Override
    public List<PmsProductOnlineDO> getProductsForSale(PageParamDTO pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
        List<PmsProductOnlineDO> pmsProductOnlineDOS = onlineDao.selectAll();
        return pmsProductOnlineDOS;
    }

    @Override
    public List<PmsProductOnlineDO> getProductsForSaleForUser(PageParamDTO pageParam, String openId) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
        List<PmsProductOnlineDO> productsForSaleForUser = onlineDao.getProductsForSaleForUser(openId);
        return productsForSaleForUser;
    }

    /**
     * 获取在售商品详情
     * @param productId
     * @param openId
     * @return
     */
    @Override
    public PmsProductOnlineDO getProductByProductId(long productId, String openId) {
        System.out.println("openid:" + openId);
        PmsProductOnlineDO pmsProductOnlineDO = onlineDao.selectByProductId(productId);
        updateVisitorService.updateVisitorIfValid(productId, openId);
        return pmsProductOnlineDO;
    }

    /**
     * 同时更新mysql和redis中的商品数据
     * @param product
     * @return
     */
    @Override
    public boolean updateProductByProductId(PmsProductOnlineDO product) {
        if (onlineDao.updateByProductId(product) != 1){
            return false;
        }
        updateVisitorService.updateIfExist(transferUtilForProduct.transferOnlineToEssential(product));
        return true;
    }


}
