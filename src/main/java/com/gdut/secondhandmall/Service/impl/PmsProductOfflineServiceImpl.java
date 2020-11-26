package com.gdut.secondhandmall.Service.impl;



import com.gdut.secondhandmall.Dao.PmsProductOfflineDao;
import com.gdut.secondhandmall.Do.PmsProductOfflineDO;
import com.gdut.secondhandmall.Dto.PageParamDTO;
import com.gdut.secondhandmall.Service.PmsProductOfflineService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/11-21:22
 * @description
 **/
@Service
public class PmsProductOfflineServiceImpl implements PmsProductOfflineService {
    @Autowired
    PmsProductOfflineDao offlineDao;

    @Override
    public List<PmsProductOfflineDO> getProductsSold(PageParamDTO pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
        List<PmsProductOfflineDO> pmsProductOfflineDOS = offlineDao.selectAll();
        return pmsProductOfflineDOS;
    }

    @Override
    public List<PmsProductOfflineDO> getProductsSoldForUser(PageParamDTO pageParam, String openId) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
        List<PmsProductOfflineDO> productsSoldForUser = offlineDao.getProductsSoldForUser(openId);
        return productsSoldForUser;
    }
}
