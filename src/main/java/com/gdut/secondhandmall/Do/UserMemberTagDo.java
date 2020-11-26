package com.gdut.secondhandmall.Do;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMemberTagDo {
    private Long id;
    private String buyNumber;
    private String sellNumber;
    private String publishNumber;
    private String collectionNumber;
}
