package com.example.bank.model;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TransferResponse implements Serializable {
    private Integer approvedAppId;
    private Integer code;
    private String comment;
}
