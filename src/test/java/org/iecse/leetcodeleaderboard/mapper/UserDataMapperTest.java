package org.iecse.leetcodeleaderboard.mapper;

import org.iecse.leetcodeleaderboard.dto.QuestionCount;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.entity.CurrentUserProfileState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserDataMapperTest {

    @Test
    void toUserProfileShouldMapAllDifficulties() {
        UserData userData = new UserData();
        userData.setUserName("alice");
        userData.setNumAcceptedQuestions(List.of(
                question("EASY", 10),
                question("MEDIUM", 20),
                question("HARD", 30)
        ));

        CurrentUserProfileState mapped = UserDataMapper.toUserProfile(userData);

        assertThat(mapped.getLeetcodeId()).isEqualTo("alice");
        assertThat(mapped.getEasy()).isEqualTo(10);
        assertThat(mapped.getMedium()).isEqualTo(20);
        assertThat(mapped.getHard()).isEqualTo(30);
        assertThat(mapped.isActive()).isTrue();
        assertThat(mapped.getLastUpdated()).isNotNull();
    }

    @Test
    void toUserProfileShouldTreatUnknownDifficultyAsHard() {
        UserData userData = new UserData();
        userData.setUserName("bob");
        userData.setNumAcceptedQuestions(List.of(question("UNKNOWN", 99)));

        CurrentUserProfileState mapped = UserDataMapper.toUserProfile(userData);

        assertThat(mapped.getEasy()).isZero();
        assertThat(mapped.getMedium()).isZero();
        assertThat(mapped.getHard()).isEqualTo(99);
    }

    @Test
    void toUserProfileWithExistingStateShouldUpdateFields() {
        UserData userData = new UserData();
        userData.setNumAcceptedQuestions(List.of(
                question("EASY", 1),
                question("MEDIUM", 2),
                question("HARD", 3)
        ));

        CurrentUserProfileState state = CurrentUserProfileState.builder()
                .leetcodeId("old")
                .easy(100)
                .medium(100)
                .hard(100)
                .isActive(false)
                .build();

        CurrentUserProfileState mapped = UserDataMapper.toUserProfile(userData, state);

        assertThat(mapped).isSameAs(state);
        assertThat(mapped.getEasy()).isEqualTo(1);
        assertThat(mapped.getMedium()).isEqualTo(2);
        assertThat(mapped.getHard()).isEqualTo(3);
        assertThat(mapped.isActive()).isTrue();
        assertThat(mapped.getLastUpdated()).isNotNull();
    }

    private static QuestionCount question(String difficulty, int count) {
        QuestionCount questionCount = new QuestionCount();
        questionCount.setDifficulty(difficulty);
        questionCount.setCount(count);
        return questionCount;
    }
}
