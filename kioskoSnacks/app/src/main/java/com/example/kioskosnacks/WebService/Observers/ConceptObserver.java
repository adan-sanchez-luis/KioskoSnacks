package com.example.kioskosnacks.WebService.Observers;

import com.example.kioskosnacks.WebService.Responses.ConceptResponse;

public interface ConceptObserver {
    void onSuccessConcept(ConceptResponse succesResponse);
    void onErrorConcept(ConceptResponse errorResponse);
}
