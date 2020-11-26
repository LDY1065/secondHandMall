package com.gdut.secondhandmall.Service.impl;



import com.gdut.secondhandmall.Dao.PmsProductOnlineDao;
import com.gdut.secondhandmall.Dto.ProductEssentialsDTO;
import com.gdut.secondhandmall.Service.ScheduleRefreshService;
import com.gdut.secondhandmall.Util.ConstanForProduct;
import com.gdut.secondhandmall.Util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/10-20:50
 * @description 每隔一个小时更新热销商品
 **/
@Service
public class ScheduleRefreshServiceImpl implements ScheduleRefreshService {
    @Autowired
    PmsProductOnlineDao onlineDao;
    @Autowired
    RedisUtil redisUtil;

    @Scheduled(cron = "*/30 * * * * *")
    @Override
    public boolean updateBestSellers() {
        HashMap<String, Object> bestSellersmap = new HashMap<>();
        List<ProductEssentialsDTO> bestSellersList = onlineDao.selectessentialsSortedByVisitors();
        if (redisUtil.hasKey(ConstanForProduct.REDIS_KEY)){
            redisUtil.del(ConstanForProduct.REDIS_KEY);
        }
        for (ProductEssentialsDTO productEssentialsDTO : bestSellersList) {
            bestSellersmap.put(String.valueOf(productEssentialsDTO.getProductId()), productEssentialsDTO);
        }
        return redisUtil.hmset(ConstanForProduct.REDIS_KEY, bestSellersmap);
    }
}
