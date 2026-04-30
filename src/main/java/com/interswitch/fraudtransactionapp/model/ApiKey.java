package com.interswitch.fraudtransactionapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ApiKey extends AuditBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "[key]")
    private String key;

    @Column(name = "key_hash", unique = true)
    private String keyHash;
    private String prefix;
    private boolean active;

    private String ownerName;
}