package com.example.lab1.retrofit;

import com.example.lab1.dto.TransferRequest;
import com.example.lab1.dto.TransferResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BankService {

    @GET("/checkBalance")
    public Call<Integer> getBalance(@Query("cardNumber") String cardNumber);

    @POST("/makeTransfer")
    public Call<TransferResponse> makeTransfer(@Body TransferRequest transferRequest);
}
