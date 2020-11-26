package com.gdut.secondhandmall.Controller;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdut.secondhandmall.Config.myEndpointConfigure;
import com.gdut.secondhandmall.Util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@ServerEndpoint(value = "/chat/connection/{sessionKey}", configurator = myEndpointConfigure.class)
public class WebSocketController {

    private static Map<String, Session> userMap=new ConcurrentHashMap();
    private RedisUtil redisUtil;

    public WebSocketController(@Autowired RedisUtil redisUtil){
        this.redisUtil=redisUtil;
    }

    @OnOpen
    public void onOpen(Session session,@PathParam("sessionKey") String sessionKey){
        String openId=redisUtil.get(sessionKey).toString();
        userMap.put(openId,session);
        try {
            String jsonMessage=redisUtil.get(openId).toString();
            session.getBasicRemote().sendText(jsonMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session){
        for(Map.Entry<String,Session> entry:userMap.entrySet()){
            if(entry.getValue()==session){
                userMap.remove(entry.getKey());
                break;
            }
        }
    }

    @OnMessage
    public void onMessage(String message){
        try{
            Map messageMap=new ObjectMapper().readValue(message, HashMap.class);
            String srcOpenId=redisUtil.get(messageMap.get("sessionKey").toString()).toString();
            String srcNickName=messageMap.get("srcNickName").toString();
            String tarOpenId=messageMap.get("tarOpenId").toString();
            //String tarNickName=messageMap.get("tarNickName").toString();
            String msg=messageMap.get("message").toString();
            Map<String,String> map=new HashMap<>();
            map.put("srcOpenId",srcOpenId);
            map.put("srcNickName",srcNickName);
            map.put("message",msg);
            String jsonMessage=new ObjectMapper().writeValueAsString(map);
            if(userMap.containsKey(tarOpenId)){
                userMap.get(tarOpenId).getBasicRemote().sendText(jsonMessage);
            }else{
                redisUtil.lSet(tarOpenId,jsonMessage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error){
        error.printStackTrace();
    }


}
