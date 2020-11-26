package com.gdut.secondhandmall.Service;

import com.gdut.secondhandmall.Do.CommentDo;
import org.springframework.web.multipart.MultipartFile;

public interface OmsCommentService {
    public CommentDo getComment(String orderId);

    public boolean publishComment(String sessionKey, String orderId, String comment, MultipartFile[] picture, String type);


}
