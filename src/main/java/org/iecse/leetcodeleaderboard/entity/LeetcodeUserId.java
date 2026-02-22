package org.iecse.leetcodeleaderboard.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Table(name="leetcode_user_id")
@AllArgsConstructor
@Builder
public class LeetcodeUserId {
    @Id
    private String userId;
}
