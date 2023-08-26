package com.example.kioskosnacks.WebService.Observers;

import com.example.kioskosnacks.WebService.Responses.SaveConceptResponse;

public interface SaveConceptObserver {
    void onSuccessSaveConcept(SaveConceptResponse succesResponse);
    void onErrorSaveConcept(SaveConceptResponse errorResponse);
}
