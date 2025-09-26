package com.exe.carenest.authorizeservice.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class Account implements UserDetails {
    @Id
    private String id;
    private String username;
    private String fullName;
    private String password;
    private String email;
    private Gender gender; // Maie , Female, Other
    private Timestamp dateOfBirth;
    private String nationality;
    private String permanentAddress;
    private String homeTown;
    private String issuedDate;
    private String issuedBy;
    private String imgUrl;

    @Column(name = "is_active")
    private boolean is_active;
    private boolean status;
    
    @Enumerated(EnumType.STRING)
    private RegisterStatus registerStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    private ShopRole shopRole; // Here is define where shop can assign role for staff if they want

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> this.role != null ? this.role.getName() : "");
    }

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
