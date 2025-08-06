package com.exe.carenest.authorizeservice.userManagement.model;

import com.exe.carenest.authorizeservice.authManagement.model.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "shop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    private String shopName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account owner;

    private String description;

    private String password; //Password riêng cho cửa hàng khi đăng nhập dashboard
    private boolean status;
    private String imgUrl;
    private String workingDays;
}