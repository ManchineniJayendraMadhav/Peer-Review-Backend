package com.peercollab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    @JsonIgnore
    private String password;

    @NotBlank
    private String role; // 'ROLE_STUDENT' or 'ROLE_TEACHER'

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
}
