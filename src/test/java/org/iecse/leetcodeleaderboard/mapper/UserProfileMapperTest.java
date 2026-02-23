package org.iecse.leetcodeleaderboard.mapper;

import org.iecse.leetcodeleaderboard.dto.UserProfileDto;
import org.iecse.leetcodeleaderboard.entity.CurrentUserProfileState;
import org.iecse.leetcodeleaderboard.entity.DailyUserProfileState;
import org.iecse.leetcodeleaderboard.entity.MonthlyUserProfileState;
import org.iecse.leetcodeleaderboard.entity.WeeklyUserProfileState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileMapperTest {

    @Test
    void shouldMapCurrentUserProfileState() {
        CurrentUserProfileState state = CurrentUserProfileState.builder()
                .leetcodeId("current")
                .easy(1)
                .medium(2)
                .hard(3)
                .build();

        UserProfileDto dto = UserProfileMapper.userProfileToDto(state);

        assertThat(dto.getLeetcodeId()).isEqualTo("current");
        assertThat(dto.getEasy()).isEqualTo(1);
        assertThat(dto.getMedium()).isEqualTo(2);
        assertThat(dto.getHard()).isEqualTo(3);
    }

    @Test
    void shouldMapDailyUserProfileState() {
        DailyUserProfileState state = DailyUserProfileState.builder()
                .leetcodeId("daily")
                .easy(4)
                .medium(5)
                .hard(6)
                .build();

        UserProfileDto dto = UserProfileMapper.userProfileToDto(state);

        assertThat(dto.getLeetcodeId()).isEqualTo("daily");
        assertThat(dto.getEasy()).isEqualTo(4);
        assertThat(dto.getMedium()).isEqualTo(5);
        assertThat(dto.getHard()).isEqualTo(6);
    }

    @Test
    void shouldMapWeeklyUserProfileState() {
        WeeklyUserProfileState state = WeeklyUserProfileState.builder()
                .leetcodeId("weekly")
                .easy(7)
                .medium(8)
                .hard(9)
                .build();

        UserProfileDto dto = UserProfileMapper.userProfileToDto(state);

        assertThat(dto.getLeetcodeId()).isEqualTo("weekly");
        assertThat(dto.getEasy()).isEqualTo(7);
        assertThat(dto.getMedium()).isEqualTo(8);
        assertThat(dto.getHard()).isEqualTo(9);
    }

    @Test
    void shouldMapMonthlyUserProfileState() {
        MonthlyUserProfileState state = MonthlyUserProfileState.builder()
                .leetcodeId("monthly")
                .easy(10)
                .medium(11)
                .hard(12)
                .build();

        UserProfileDto dto = UserProfileMapper.userProfileToDto(state);

        assertThat(dto.getLeetcodeId()).isEqualTo("monthly");
        assertThat(dto.getEasy()).isEqualTo(10);
        assertThat(dto.getMedium()).isEqualTo(11);
        assertThat(dto.getHard()).isEqualTo(12);
    }
}
