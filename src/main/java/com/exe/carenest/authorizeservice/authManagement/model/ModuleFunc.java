package com.exe.carenest.authorizeservice.authManagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "module")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleFunc {

    @Id
    @Column(name = "url_pattern", nullable = false, unique = true)
    private String urlPattern;

    @Column(nullable = false)
    private String name;
}