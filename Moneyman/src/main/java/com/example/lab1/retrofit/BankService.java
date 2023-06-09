package com.example.lab1.retrofit;

import com.example.lab1.dto.TransferRequest;
import com.example.lab1.dto.TransferResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BankService {
    @POST("/makeTransfer")
    public Call<TransferResponse> makeTransfer(@Body TransferRequest transferRequest);
}
