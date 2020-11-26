package com.gdut.secondhandmall.Controller;




import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.gdut.secondhandmall.Bo.loginResponseBo;
import com.gdut.secondhandmall.Bo.userCollectionBo;
import com.gdut.secondhandmall.Bo.wxResponseBo;
import com.gdut.secondhandmall.Do.UserInfoDo;
import com.gdut.secondhandmall.Do.UserMemberTagDo;
import com.gdut.secondhandmall.Service.UmsUserService;
import com.gdut.secondhandmall.Util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/userService")
public class UserController {
    private RestTemplate restTemplate;
    private RedisUtil redisUtil;
    private UmsUserService umsUserService;

    public UserController(@Autowired RestTemplate restTemplate, RedisUtil redisUtil, UmsUserService umsUserService){
        this.restTemplate=restTemplate;
        this.redisUtil=redisUtil;
        this.umsUserService=umsUserService;
    }



    /**
     * 用户登录API
     * @Param code 临时登录凭证
     * @return 用户sessionKey及错误信息
     * @Author 邱泽滨
     */
    @GetMapping("/sessionKey")
    public loginResponseBo login(String code){
        loginResponseBo loginResponseBo=new loginResponseBo();
        if(code==null||code.equals("")){
            loginResponseBo.setSessionKey("");
            loginResponseBo.setErrorCode("1");
            loginResponseBo.setErrorMsg("参数为空");
            return loginResponseBo;
        }
        String url="https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={js_code}&grant_type={grant_type}";
        Map<String,String> param =new HashMap<>();
        param.put("appid","wxc550a6ab7e38162a");
        param.put("secret","e980e0dca99c7b1b0dc8bdb890ba0dca");
        param.put("js_code",code);
        param.put("grant_type","authorization_code");
        wxResponseBo wxResponseBo=restTemplate.getForObject(url, wxResponseBo.class,param);
        if(wxResponseBo.getOpenid()!=null){
            String sessionKey=wxResponseBo.getSession_key()+ UUID.randomUUID().toString();
            umsUserService.logUserLogin(wxResponseBo.getOpenid());
            if(umsUserService.getUser(wxResponseBo.getOpenid())==null){
                umsUserService.initUser(wxResponseBo.getOpenid());
            }
            redisUtil.set(sessionKey,wxResponseBo.getOpenid());
            redisUtil.expire(sessionKey,1800);
            loginResponseBo.setSessionKey(sessionKey);
        }else{
            loginResponseBo.setErrorCode(wxResponseBo.getErrcode());
            loginResponseBo.setErrorMsg(wxResponseBo.getErrmsg());
        }
        return loginResponseBo;
    }

    /**
     *
     * @param jsonStr：
     *           字段：nickName--用户昵称
     *               avatarUrl--头像url
     *               gender--性别
     *               country--国家
     *               province--省份
     *               city--城市
     *               language--语言
     *               sessionKey--登录令牌
     * @return  返回错误码和错误信息
     * @Author 邱泽滨
     */
    @PostMapping("/userInfo")
    public Map<String, String> userInfo(@RequestBody String jsonStr){
        Map<String,String> map=new HashMap<>();
        try {
            Map<String,String> param=new ObjectMapper().readValue(jsonStr,HashMap.class);
            if(!param.containsKey("sessionKey")){
                map.put("errorCode","1");
                map.put("errorMsg","参数不能为空");
            }else if(redisUtil.get(param.get("sessionKey"))==null){
                map.put("errorCode","1");
                map.put("errorMsg","用户登录过期，请重新登录");
            }else{
                String openId=redisUtil.get(param.get("sessionKey")).toString();
                param.remove("sessionKey");
                param.put("openId",openId);
                String userInfoStr=new ObjectMapper().writeValueAsString(param);
                UserInfoDo userInfo=new ObjectMapper().readValue(userInfoStr, UserInfoDo.class);
                if(umsUserService.updateUserInfo(userInfo)==1){
                    map.put("errorCode","0");
                    map.put("errorMsg","用户信息更新成功");
                }else {
                    map.put("errorCode","1");
                    map.put("errorMsg","用户信息更新失败，请重试");
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     *
     * @param sessionKey 登录令牌
     * @return  返回用户消费标记
     * @Author  邱泽滨
     */
    @GetMapping("/memberTag")
    public UserMemberTagDo getUserMemberTagDo(String sessionKey){
        return umsUserService.getUserMemberTagDO(sessionKey);
    }

    @GetMapping("/loginStatus")
    public Map<String,String> getUserLoginStatus(String sessionKey){
        Map<String,String> map=new HashMap<>();
        if(redisUtil.get(sessionKey)==null){
            map.put("errorCode","1");
            map.put("errorMsg","用户登录失效");
            return map;
        }
        map.put("errorCode","0");
        map.put("errorMsg","用户登录有效");
        return map;
    }


    /**
     *
     * @param sessionKey  登录令牌
     * @param page  当前第几页
     * @param size  每页的个数
     * @return  返回用户收藏列表
     * @Author  邱泽滨
     */
    @GetMapping("/userCollection")
    public List<userCollectionBo> getUserCollection(String sessionKey, String page, String size){
        return umsUserService.getUserCollectionBo(sessionKey,page,size);
    }


    /**
     *
     * @param jsonStr：
     *            字段：sessionKey--登录令牌
     *                 collectionId--收藏对象id
     * @return  返回状态码和状态信息
     * @Author  邱泽滨
     */
    @PostMapping("/collection")
    public Map<String,String> collectUser(@RequestBody String jsonStr){
        Map<String,String> map=new HashMap<>();
        try {
            Map<String,String> param=new ObjectMapper().readValue(jsonStr,HashMap.class);
            if(umsUserService.collectUser(param.get("sessionKey"),param.get("collectionId"))!=1){
                map.put("errorCode","1");
                map.put("errorMsg","收藏失败");
            }
            map.put("errorCode","0");
            map.put("errorMsg","收藏成功");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }
}
