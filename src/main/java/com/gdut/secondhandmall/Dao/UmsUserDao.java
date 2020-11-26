package com.gdut.secondhandmall.Dao;




import com.gdut.secondhandmall.Bo.userCollectionBo;
import com.gdut.secondhandmall.Do.UserInfoDo;
import com.gdut.secondhandmall.Do.UserMemberTagDo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UmsUserDao {

    @Insert("insert into ums_member_login_log (id,openid,create_time) values (null,#{openId},#{createTime})")
    public int insertUserLoginLog(@Param("openId") String openId, @Param("createTime") String createTime);

    @Insert("insert into ums_userinfo (id,openid,nick_name,avatar_url,gender,country,province,city,language) values (null,#{openId}," +
            "null,null,null,null,null,null,null)")
    public int insertUserInformation(@Param("openId") String openId);

    @Select("select ums_member_tag.* from ums_member_tag_relation left join ums_member_tag on ums_member_tag.id=ums_member_tag_relation.tag_id where openid=#{openId}")
    public UserMemberTagDo selectUserMemberTagDo(String openId);

    @Insert("insert into ums_member_tag (id,buy_number,sell_number,publish_number,collection_number) values " +
            "(null,0,0,0,0)")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    public int insertUserTag(@Param("userMemberTagDo") UserMemberTagDo userMemberTagDo);

    @Insert("insert into ums_member_tag_relation (id,openId,tag_id) values (null,#{openId},#{tagId})")
    public int insertUserTagRelation(@Param("openId") String openId,@Param("tagId") String tagId);

    @Select("select * from ums_userinfo where openId=#{openId}")
    public UserInfoDo selectUser(String openId);

    @Update("update ums_userinfo set nick_name=#{userInfoDo.nickName},avatar_url=#{userInfoDo.avatarUrl},gender=#{userInfoDo.gender},country=#{userInfoDo.country},province=#{userInfoDo.province},city=#{userInfoDo.city},language=#{userInfoDo.language} where openId=#{userInfoDo.openId}")
    public int updateUserInfo(@Param("userInfoDo") UserInfoDo userInfoDo);

    @Select("select ums_userInfo.nick_name,ums_userInfo.avatar_url from ums_member_collection " +
            "left join ums_userInfo on ums_member_collection.collection_Id=ums_userInfo.openId where ums_member_collection.openId=#{openId}")
    public List<userCollectionBo> selectUserCollection(String openId);

    @Insert("insert into ums_member_collection (id,openId,collection_id) values (null,#{openId},#{collectionId})")
    public int insertUserCollection(@Param("openId") String openId,@Param("collectionId") String collectionId);
}
