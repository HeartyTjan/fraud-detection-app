package com.interswitch.fraudtransactionapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN(EnumSet.of(Permission.GET_ALL_BLACKLISTED,
                     Permission.VIEW_FLAGGED_TRANSACTIONS
                    )
    );

    private final Set<Permission> permissions;

}

