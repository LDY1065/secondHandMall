package com.gdut.secondhandmall.Service;

import com.gdut.secondhandmall.Bo.userCollectionBo;
import com.gdut.secondhandmall.Do.UserInfoDo;
import com.gdut.secondhandmall.Do.UserMemberTagDo;

import java.util.List;

public interface UmsUserService {
    public UserInfoDo getUser(String openId);

    public int logUserLogin(String openId);

    public void initUser(String openId);

    public int updateUserInfo(UserInfoDo userInfoDo);

    public UserMemberTagDo getUserMemberTagDO(String sessionKey);

    public List<userCollectionBo> getUserCollectionBo(String sessionKey, String page, String size);

    public int collectUser(String sessionKey,String collectionId);
}
