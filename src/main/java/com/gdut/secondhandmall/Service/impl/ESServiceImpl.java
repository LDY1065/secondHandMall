package com.gdut.secondhandmall.Service.impl;



import com.gdut.secondhandmall.Dto.PageParamDTO;
import com.gdut.secondhandmall.Dto.ProductEssentialsDTO;
import com.gdut.secondhandmall.Dto.SortParamDTO;
import com.gdut.secondhandmall.Service.ESService;
import com.gdut.secondhandmall.Util.ConstanForProduct;
import com.gdut.secondhandmall.Util.ESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/16-17:34
 * @description
 **/
@Service
public class ESServiceImpl implements ESService {
    @Autowired
    ESUtil esUtil;

    @Override
    public boolean put(ProductEssentialsDTO essential) {
        try {
            esUtil.addDoc(ConstanForProduct.ELASTICSEARCH_INDEX, String.valueOf(essential.getProductId()), essential);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean put(List<ProductEssentialsDTO> essentials) {
        try {
            esUtil.bulkAdd(ConstanForProduct.ELASTICSEARCH_INDEX, essentials);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(ProductEssentialsDTO essential) {
        try {
            esUtil.deleteDoc(ConstanForProduct.ELASTICSEARCH_INDEX, String.valueOf(essential.getProductId()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean update(ProductEssentialsDTO essential) {
        try {
            esUtil.updateDoc(ConstanForProduct.ELASTICSEARCH_INDEX, String.valueOf(essential.getProductId()), essential);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<ProductEssentialsDTO> search(String key, SortParamDTO sortParam, PageParamDTO pageParam) {
        List<ProductEssentialsDTO> list = null;
        int from = (pageParam.getPage() - 1) * pageParam.getSize();
        int size = pageParam.getSize();
        try {
            list = esUtil.searchForNameAndDescription(ConstanForProduct.ELASTICSEARCH_INDEX, key, sortParam.getSort(),
                    sortParam.getOrder(), from, size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ProductEssentialsDTO> getByTag(short tag, SortParamDTO sortParam, PageParamDTO pageParam) {
        List<ProductEssentialsDTO> list = null;
        int from = (pageParam.getPage() - 1) * pageParam.getSize();
        int size = pageParam.getSize();
        try {
            list = esUtil.searchByTag(ConstanForProduct.ELASTICSEARCH_INDEX, tag, sortParam.getSort(),
                    sortParam.getOrder(),
                    from, size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
