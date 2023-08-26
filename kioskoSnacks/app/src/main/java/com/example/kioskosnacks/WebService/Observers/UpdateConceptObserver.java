package com.example.kioskosnacks.WebService.Observers;

import com.example.kioskosnacks.WebService.Responses.UpdateConceptResponse;

public interface UpdateConceptObserver {
    void onSuccessUpdate(UpdateConceptResponse succesResponse);
    void onErrorUpdate(UpdateConceptResponse errorResponse);
}
