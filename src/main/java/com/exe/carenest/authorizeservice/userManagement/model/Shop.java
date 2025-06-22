package com.exe.carenest.authorizeservice.userManagement.model;

import com.exe.carenest.authorizeservice.authManagement.model.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "shop")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Shop extends Account {

    private String shopName;

    @Column(name = "description")
    private String description;

    private String phone;

    @Column(name = "status")
    private String status;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_num")
    private String bankNum;

    @Column(name = "working_day")
    private String workingDay;

    @Column(name = "hotline")
    private String hotline;

    @Column(name = "identity_card")
    private String identityCard;
}