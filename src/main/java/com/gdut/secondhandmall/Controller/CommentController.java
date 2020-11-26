package com.gdut.secondhandmall.Controller;




import com.gdut.secondhandmall.Do.CommentDo;
import com.gdut.secondhandmall.Service.OmsCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/commentService")
public class CommentController {

    private OmsCommentService omsCommentService;

    public CommentController(@Autowired OmsCommentService omsCommentService){
        this.omsCommentService=omsCommentService;
    }

    @GetMapping("/tradeComment")
    public CommentDo getComment(String orderId){
        return omsCommentService.getComment(orderId);
    }

    @PostMapping("/comment")
    public Map<String,String> publishComment(@RequestParam(name = "sessionKey",required = true) String sessionKey,
                                           @RequestParam(name="orderId",required = true) String orderId,
                                           @RequestParam(name="comment",required = true) String comment,
                                           @RequestParam(name="picture",required = false)MultipartFile[] picture,
                                           @RequestParam(name="type",required = true) String type){
        Map<String,String> map=new HashMap<>();
        if(omsCommentService.publishComment(sessionKey,orderId,comment,picture,type)){
            map.put("errorCode","0");
            map.put("errorCode","发表成功");
        }else{
            map.put("errorCode","1");
            map.put("errorCode","发表失败，参数错误");
        }
        return map;
    }
}
