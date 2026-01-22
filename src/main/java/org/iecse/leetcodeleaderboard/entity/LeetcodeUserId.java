package org.iecse.leetcodeleaderboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class LeetcodeUserId {
    @Id
    private String userId;
}
