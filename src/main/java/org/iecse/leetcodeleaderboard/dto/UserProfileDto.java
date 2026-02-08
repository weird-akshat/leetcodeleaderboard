package org.iecse.leetcodeleaderboard.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@AllArgsConstructor
@Data
@Builder
public class UserProfileDto {
    private String leetcodeId;
    private int easy;
    private int medium;
    private int hard;
}
