package org.iecse.leetcodeleaderboard.mapper;

import org.iecse.leetcodeleaderboard.dto.QuestionCount;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.entity.CurrentUserProfileState;

import java.time.LocalDateTime;

public class UserDataMapper {
    public static CurrentUserProfileState toUserProfile(UserData userData){
        int easy=0;
        int medium=0;
        int hard=0;
        for (QuestionCount questionCount : userData.getNumAcceptedQuestions()){
            if (questionCount.getDifficulty().equals("EASY")){
                easy = questionCount.getCount();
            }
            else if (questionCount.getDifficulty().equals("MEDIUM")){
                medium = questionCount.getCount();
            }
            else{
                hard = questionCount.getCount();
            }
        }
        return CurrentUserProfileState.builder().leetcodeId(userData.getUserName()).easy(easy).medium(medium).hard(hard)
                .isActive(true)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    public static CurrentUserProfileState toUserProfile(UserData userData, CurrentUserProfileState currentUserProfileState){
        int easy=0;
        int medium=0;
        int hard=0;
        for (QuestionCount questionCount : userData.getNumAcceptedQuestions()){
            if (questionCount.getDifficulty().equals("EASY")){
                easy = questionCount.getCount();
            }
            else if (questionCount.getDifficulty().equals("MEDIUM")){
                medium = questionCount.getCount();
            }
            else{
                hard = questionCount.getCount();
            }
        }
        currentUserProfileState.setEasy(easy);
        currentUserProfileState.setMedium(medium);
        currentUserProfileState.setHard(hard);
        currentUserProfileState.setActive(true);
        currentUserProfileState.setLastUpdated(LocalDateTime.now());

        return currentUserProfileState;

    }
}
