package com.exe.carenest.authorizeservice.userManagement.model;

import com.exe.carenest.authorizeservice.authManagement.model.Account;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@PrimaryKeyJoinColumn(name = "account_id")
public class Customer extends Account {

    @Column(name = "gender")
    private String gender;

    private String phone;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "point")
    private Integer point;

    @Column(name = "platform_service_id")
    private Long platformServiceId;
}