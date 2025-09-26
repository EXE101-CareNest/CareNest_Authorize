package com.exe.carenest.authorizeservice.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "role_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "module_url_pattern", referencedColumnName = "url_pattern")
    @JsonBackReference
    private ModuleFunc module;

    @Enumerated(EnumType.STRING)
    private HttpPermission httpPermission;
}