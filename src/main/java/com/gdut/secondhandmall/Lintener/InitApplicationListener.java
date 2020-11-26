package com.gdut.secondhandmall.Lintener;



import com.gdut.secondhandmall.Dto.ProductEssentialsDTO;
import com.gdut.secondhandmall.Service.PmsProductOnlineService;
import com.gdut.secondhandmall.Util.ConstanForProduct;
import com.gdut.secondhandmall.Util.ESUtil;
import com.gdut.secondhandmall.Util.TransferUtilForProduct;
import com.gdut.secondhandmall.Util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTIme 2020/8/9-17:42
 * @description 在容器启动时做初始化工作
 **/
@Component
public class InitApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    PmsProductOnlineService onlineService;
    @Autowired
    ESUtil esUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    TransferUtilForProduct transferUtilForProduct;

    /**
     * 在应用启动的时候将mysql中的数据导入ES和Redis中
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //将商品表中所有商品的概要信息取出
        ArrayList<ProductEssentialsDTO> list = (ArrayList<ProductEssentialsDTO>) onlineService.selectessentials();
        //初始化redis并将前20种商品放进redis中
        if (redisUtil.hasKey(ConstanForProduct.REDIS_KEY)){
            redisUtil.del(ConstanForProduct.REDIS_KEY);
        }
        int num = Math.min(6, list.size());
        HashMap<String, Object> map = new HashMap<>();;
        for (ProductEssentialsDTO productEssentialsDTO : list) {
            if (num <= 0){
                break;
            }
            map.put(String.valueOf(productEssentialsDTO.getProductId()), productEssentialsDTO);
            num--;
        }
        redisUtil.hmset(ConstanForProduct.REDIS_KEY, map);
        redisUtil.expire(ConstanForProduct.REDIS_KEY, 60*60*2);

        //初始化ES并将所有商品放进ES中
        try {
            if (esUtil.existsIndex(ConstanForProduct.ELASTICSEARCH_INDEX)){
                esUtil.deleteIndex(ConstanForProduct.ELASTICSEARCH_INDEX);
            }
            esUtil.createIndex(ConstanForProduct.ELASTICSEARCH_INDEX);
            esUtil.bulkAddForOnlineDTO(ConstanForProduct.ELASTICSEARCH_INDEX, list);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
