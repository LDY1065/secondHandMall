package com.gdut.secondhandmall.Service;





import com.gdut.secondhandmall.Do.PmsProductOnlineDO;
import com.gdut.secondhandmall.Dto.PageParamDTO;
import com.gdut.secondhandmall.Dto.ProductEssentialsDTO;

import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/10-11:12
 * @description
 **/
public interface PmsProductOnlineService {
    List<ProductEssentialsDTO> selectessentials();

    List<PmsProductOnlineDO> getProductsForSale(PageParamDTO pageParam);

    List<PmsProductOnlineDO> getProductsForSaleForUser(PageParamDTO pageParam, String openId);

    PmsProductOnlineDO getProductByProductId(long productId, String openId);

    boolean updateProductByProductId(PmsProductOnlineDO product);
}
