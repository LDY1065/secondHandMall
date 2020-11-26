package com.gdut.secondhandmall.Dao;




import com.gdut.secondhandmall.Do.CommentDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface OmsCommentDao {

    @Select("select Buyer_comment,Buyer_pic_url,buyer_time,Seller_comment,Seller_pic_url,seller_time from oms_comment where order_id=#{orderId}")
    public CommentDo selectComment(String orderId);

    @Update("update oms_comment set Buyer_comment=#{comment},Buyer_pic_url=#{pictureUrl},Buyer_Time=#{time} where Order_id=#{orderId}")
    public int updateBuyerComment(@Param("orderId") String orderId,@Param("comment") String comment,@Param("pictureUrl") String pictureUrl,@Param("time") String time);

    @Update("update oms_comment set Seller_comment=#{comment},Seller_pic_url=#{pictureUrl},Seller_Time=#{time} where Order_id=#{orderId}")
    public int updateSellerComment(@Param("orderId") String orderId,@Param("comment") String comment,@Param("pictureUrl") String pictureUrl,@Param("time") String time);

    @Select("select product_id from oms_order_completed where order_id=#{orderId}")
    public long selectProductId(String orderId);
}
