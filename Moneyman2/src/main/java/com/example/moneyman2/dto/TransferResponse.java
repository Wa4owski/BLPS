package com.example.moneyman2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TransferResponse implements Serializable {
    private Integer approvedAppId;
    private Integer code;
    private String comment;
}
