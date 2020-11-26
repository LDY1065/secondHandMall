package com.gdut.secondhandmall.Service;




import com.gdut.secondhandmall.Do.PmsProductVertifyRecordDO;
import com.gdut.secondhandmall.Dto.PageParamDTO;
import com.gdut.secondhandmall.Dto.VerificationDTOForProduct;

import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTime 2020/8/11-16:24
 * @description
 **/
public interface PmsProductVertifyRecordService {
    int insertNewRecord(PmsProductVertifyRecordDO record);

    List<PmsProductVertifyRecordDO> getVertifyRecords(PageParamDTO pageParam);

    List<PmsProductVertifyRecordDO> getVertifyRecordsForUser(PageParamDTO pageParam, String openId);

    PmsProductVertifyRecordDO getVerifyRecordDetail(long productId);

    boolean completeVerification(VerificationDTOForProduct verification);
}
