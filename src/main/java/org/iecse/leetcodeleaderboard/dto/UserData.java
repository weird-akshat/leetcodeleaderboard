package org.iecse.leetcodeleaderboard.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Data
public class UserData {
    private String userName;
    private List<QuestionCount> numAcceptedQuestions;
}