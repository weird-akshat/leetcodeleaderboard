package org.iecse.leetcodeleaderboard.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


//@Entity
@Data
@org.springframework.data.relational.core.mapping.Table(name="leetcode_user_id")
public class LeetcodeUserId {
    @Id
//    @jakarta.persistence.Id
    private String userId;
}
