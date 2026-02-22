package org.iecse.leetcodeleaderboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateLeetcodeIdDto {
    String newLeetcodeId;
}
