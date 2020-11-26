package com.gdut.secondhandmall.Service;




import com.gdut.secondhandmall.Dto.PageParamDTO;
import com.gdut.secondhandmall.Dto.ProductEssentialsDTO;
import com.gdut.secondhandmall.Dto.SortParamDTO;

import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/15-17:13
 * @description
 **/
public interface ESService {
    boolean put(ProductEssentialsDTO essential);

    boolean put(List<ProductEssentialsDTO> essentials);

    boolean delete(ProductEssentialsDTO essential);

    boolean update(ProductEssentialsDTO essential);

    List<ProductEssentialsDTO> search(String key, SortParamDTO sortParam, PageParamDTO pageParam);

    List<ProductEssentialsDTO> getByTag(short tag, SortParamDTO sortParam, PageParamDTO pageParam);
}
