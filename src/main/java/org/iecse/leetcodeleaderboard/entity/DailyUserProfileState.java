package org.iecse.leetcodeleaderboard.entity;



import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name="daily_user_profile_state")
@Data
@Builder
public class DailyUserProfileState {
    @Id
    private Long id;
    private String leetcodeId;
    private int easy;
    private int medium;
    private int hard;
    private LocalDateTime updatedAt;
    private boolean isActive;

}
