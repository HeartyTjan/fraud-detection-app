package com.interswitch.fraudtransactionapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_card")
@Setter
@Getter
public class BlacklistedCard {

    @Id
    private String cardNo;
    private String reason;
    private LocalDateTime lastUpdated;


}