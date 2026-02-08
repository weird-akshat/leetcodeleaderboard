package org.iecse.leetcodeleaderboard.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name="weekly_user_profile_state")
@Data
@Builder
public class WeeklyUserProfileState  {
    @Id
    private Long id;
    private String leetcodeId;
    private int easy;
    private int medium;
    private int hard;

}
