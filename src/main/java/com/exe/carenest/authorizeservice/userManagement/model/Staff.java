package com.exe.carenest.authorizeservice.userManagement.model;

import com.exe.carenest.authorizeservice.authManagement.model.Account;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "staff")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Staff extends Account {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String phone;

    @Column(name = "gender")
    private String gender;

    @Column(name = "position")
    private String position;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "hired_at")
    private LocalDate hiredAt;

    @Column(name = "shop_address")
    private String shopAddress;

    @Column(name = "platform_service_id")
    private Long platformServiceId; // FK, chưa rõ entity PlatformService nên để Long


}