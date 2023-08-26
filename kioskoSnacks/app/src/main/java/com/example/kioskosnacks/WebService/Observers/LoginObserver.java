package com.example.kioskosnacks.WebService.Observers;

import com.example.kioskosnacks.WebService.Responses.LoginResponse;

public interface LoginObserver {
    void onSuccessLogin(LoginResponse succesResponse);
    void onErrorLogin(LoginResponse errorResponse);
}
