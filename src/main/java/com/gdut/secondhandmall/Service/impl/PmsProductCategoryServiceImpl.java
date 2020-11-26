package com.gdut.secondhandmall.Service.impl;



import com.gdut.secondhandmall.Dao.PmsProductCategoryDao;
import com.gdut.secondhandmall.Do.PmsProductCategoryDO;
import com.gdut.secondhandmall.Service.PmsProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/11-16:40
 * @description
 **/
@Service
public class PmsProductCategoryServiceImpl implements PmsProductCategoryService {
    @Autowired
    PmsProductCategoryDao categoryDao;

    @Override
    public List<String> getSecondaryDirectoryByPrimaryDirectory(String primaryDirectory) {
        List<String> sList = new ArrayList<>();
        List<PmsProductCategoryDO> list = categoryDao.getSecondaryDirectoryByPrimaryDirectory(primaryDirectory);
        for (PmsProductCategoryDO pmsProductCategoryDO : list) {
            sList.add(pmsProductCategoryDO.getSecondaryDirectory());
        }
        return sList;
    }
}
