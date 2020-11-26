package com.gdut.secondhandmall.Do;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDo {
    private String buyerComment;
    private String buyerPicUrl;
    private String buyerTime;
    private String sellerComment;
    private String sellerPicUrl;
    private String sellerTime;
}
