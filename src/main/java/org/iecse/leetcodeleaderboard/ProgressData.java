package org.iecse.leetcodeleaderboard.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Data
public class ProgressData {
    public List<QuestionCount> numAcceptedQuestions;
}