package com.gdut.secondhandmall.Service;





import com.gdut.secondhandmall.Do.PmsProductOfflineDO;
import com.gdut.secondhandmall.Dto.PageParamDTO;

import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/11-21:21
 * @description
 **/
public interface PmsProductOfflineService {
    List<PmsProductOfflineDO> getProductsSold(PageParamDTO pageParam);

    List<PmsProductOfflineDO> getProductsSoldForUser(PageParamDTO pageParam, String openId);
}
