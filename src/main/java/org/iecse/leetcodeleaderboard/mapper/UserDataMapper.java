package org.iecse.leetcodeleaderboard.mapper;

import org.iecse.leetcodeleaderboard.dto.QuestionCount;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.entity.UserProfile;

public class UserDataMapper {
    public static UserProfile toUserProfile(UserData userData){
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
        return UserProfile.builder().leetcodeId(userData.getUserName()).easy(easy).medium(medium).hard(hard) .build();
    }
    public static UserProfile toUserProfile(UserData userData,UserProfile userProfile){
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
        userProfile.setEasy(easy);
        userProfile.setMedium(medium);
        userProfile.setHard(hard);

        return userProfile;

    }
}
