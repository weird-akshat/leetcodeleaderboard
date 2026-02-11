package org.iecse.leetcodeleaderboard.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@org.springframework.data.relational.core.mapping.Table(name="app_user")
public class AppUser {

    @Id
    private Long id;
    private String email;
    private String fullName;
    private String password;
    private String role;

}

