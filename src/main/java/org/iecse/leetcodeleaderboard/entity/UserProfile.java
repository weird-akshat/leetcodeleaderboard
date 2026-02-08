package org.iecse.leetcodeleaderboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name="user_profile")
@Data
@Builder
public class UserProfile {
    @Id
    private Long id;
    private String leetcodeId;
    private int easy;
    private int medium;
    private int hard;

}
