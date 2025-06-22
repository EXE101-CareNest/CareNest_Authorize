package com.exe.carenest.authorizeservice.userManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sub-address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
