package com.gdut.secondhandmall.Service.impl;




import com.gdut.secondhandmall.Dao.OmsCommentDao;
import com.gdut.secondhandmall.Do.CommentDo;
import com.gdut.secondhandmall.Service.OmsCommentService;
import com.gdut.secondhandmall.Util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class OmsCommentServiceImpl implements OmsCommentService {
    private OmsCommentDao OmsCommentDao;
    private RedisUtil redisUtil;

    public OmsCommentServiceImpl(@Autowired OmsCommentDao OmsCommentDao, RedisUtil redisUtil){
        this.OmsCommentDao = OmsCommentDao;
        this.redisUtil=redisUtil;
    }

    public CommentDo getComment(String orderId){
        return OmsCommentDao.selectComment(orderId);
    }

    public boolean publishComment(String sessionKey,String orderId,String comment,MultipartFile[] picture,String type){
        String openId=redisUtil.get(sessionKey).toString();
        StringBuffer fileName=new StringBuffer();
        StringBuffer pictureUrl=new StringBuffer();
        if(picture!=null){
            for(MultipartFile multipartFile:picture){
                fileName.append(UUID.randomUUID().toString());
                if (multipartFile.getContentType().equals("image/png")) {
                    fileName.append(".png");
                } else if (multipartFile.getContentType().equals("image/jpeg")) {
                    fileName.append(".jpeg");
                } else {
                    return false;
                }
                long productId= OmsCommentDao.selectProductId(orderId);
                File file=new File("/project/"+productId+"/comment",fileName.toString());
                try{
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    file.createNewFile();
                    multipartFile.transferTo(file);
                    pictureUrl.append("/static/image/"+productId+"/comment/"+fileName+";");
                }catch (Exception e){
                    e.printStackTrace();
                }
                fileName.setLength(0);
            }
        }
        if(type.equals("buyer")){
            OmsCommentDao.updateBuyerComment(orderId,comment, pictureUrl.toString(),new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        }else if(type.equals("seller")){
            OmsCommentDao.updateSellerComment(orderId,comment, pictureUrl.toString(),new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        }else{
            return false;
        }
        return true;
    }
}
