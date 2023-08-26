package com.example.kioskosnacks.WebService.Observers;

import com.example.kioskosnacks.WebService.Responses.SupplyResponse;

public interface SupplyObserver {
    void onSuccessSuply(SupplyResponse succesResponse);
    void onErrorSupply(SupplyResponse errorResponse);
}
