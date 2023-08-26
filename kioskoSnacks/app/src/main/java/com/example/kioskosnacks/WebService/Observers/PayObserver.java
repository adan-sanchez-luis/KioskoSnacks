package com.example.kioskosnacks.WebService.Observers;

import com.example.kioskosnacks.WebService.Responses.PayConceptResponse;

public interface PayObserver {
    void onSuccessPayConcept(PayConceptResponse succesResponse);

    void onErrorPayConcept(PayConceptResponse errorResponse);
}
