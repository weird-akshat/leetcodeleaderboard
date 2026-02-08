package org.iecse.leetcodeleaderboard.mapper;

import org.iecse.leetcodeleaderboard.dto.UserProfileDto;
import org.iecse.leetcodeleaderboard.entity.CurrentUserProfileState;
import org.iecse.leetcodeleaderboard.entity.DailyUserProfileState;
import org.iecse.leetcodeleaderboard.entity.MonthlyUserProfileState;
import org.iecse.leetcodeleaderboard.entity.WeeklyUserProfileState;

public class UserProfileMapper {
    public static UserProfileDto userProfileToDto(CurrentUserProfileState currentUserProfileState){
        return new UserProfileDto(currentUserProfileState.getLeetcodeId(), currentUserProfileState.getEasy(), currentUserProfileState.getMedium(), currentUserProfileState.getHard());
    }
    public static UserProfileDto userProfileToDto(WeeklyUserProfileState weeklyUserProfileState){
        return new UserProfileDto(weeklyUserProfileState.getLeetcodeId(), weeklyUserProfileState.getEasy(), weeklyUserProfileState.getMedium(), weeklyUserProfileState.getHard());
    }
    public static UserProfileDto userProfileToDto(DailyUserProfileState dailyUserProfileState){
        return new UserProfileDto(dailyUserProfileState.getLeetcodeId(), dailyUserProfileState.getEasy(), dailyUserProfileState.getMedium(), dailyUserProfileState.getHard());
    }
    public static UserProfileDto userProfileToDto(MonthlyUserProfileState monthlyUserProfileState){
        return new UserProfileDto(monthlyUserProfileState.getLeetcodeId(), monthlyUserProfileState.getEasy(), monthlyUserProfileState.getMedium(), monthlyUserProfileState.getHard());
    }
}
