package com.gdut.secondhandmall.Service.impl;






import com.gdut.secondhandmall.Bo.userCollectionBo;
import com.gdut.secondhandmall.Dao.UmsUserDao;
import com.gdut.secondhandmall.Do.UserInfoDo;
import com.gdut.secondhandmall.Do.UserMemberTagDo;
import com.gdut.secondhandmall.Service.UmsUserService;
import com.gdut.secondhandmall.Util.RedisUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UmsUserServiceImpl implements UmsUserService {

    private UmsUserDao UmsUserDao;
    private RedisUtil redisUtil;

    public UmsUserServiceImpl(@Autowired UmsUserDao UmsUserDao, RedisUtil redisUtil){
        this.UmsUserDao = UmsUserDao;
        this.redisUtil=redisUtil;
    }

    public UserInfoDo getUser(String openId){
        return UmsUserDao.selectUser(openId);
    }

    public int logUserLogin(String openId){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return UmsUserDao.insertUserLoginLog(openId,simpleDateFormat.format(new Date()));
    }

    public void initUser(String openId){
        UserMemberTagDo userMemberTagDo=new UserMemberTagDo();
        UmsUserDao.insertUserTag(userMemberTagDo);
        Long autoId=userMemberTagDo.getId();
        UmsUserDao.insertUserTagRelation(openId,autoId.toString());
        UmsUserDao.insertUserInformation(openId);
    }


    public int updateUserInfo(UserInfoDo userInfoDo){
        return UmsUserDao.updateUserInfo(userInfoDo);
    }

    public UserMemberTagDo getUserMemberTagDO(String sessionKey){
        if(redisUtil.get(sessionKey).toString()==null)
            return null;
        return UmsUserDao.selectUserMemberTagDo(redisUtil.get(sessionKey).toString());
    }

    public List<userCollectionBo> getUserCollectionBo(String sessionKey, String page, String size){
        String openId=redisUtil.get(sessionKey).toString();
        if(page!=null&&size!=null){
            PageHelper.startPage(Integer.valueOf(page),Integer.valueOf(size));
        }
        return UmsUserDao.selectUserCollection(openId);
    }

    public int collectUser(String sessionKey,String collectionId){
        return UmsUserDao.insertUserCollection(redisUtil.get(sessionKey).toString(),collectionId);
    }
}
