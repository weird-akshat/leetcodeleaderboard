package org.iecse.leetcodeleaderboard.security.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("app_users")

public class AppUser {
    @Id
    private Long id;
    private String username;
    private String password;
    private String leetcodeId;
    private String role;
    private boolean active;
}