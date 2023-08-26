package com.example.kioskosnacks.WebService.Observers;

import com.example.kioskosnacks.WebService.Responses.ReportResponse;

public interface ReportObserver {
    void onSuccessReport(ReportResponse succesResponse);
    void onErrorReport(ReportResponse errorResponse);
}
