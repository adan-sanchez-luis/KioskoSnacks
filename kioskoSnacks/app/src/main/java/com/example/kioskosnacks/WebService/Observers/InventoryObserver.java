package com.example.kioskosnacks.WebService.Observers;

import com.example.kioskosnacks.WebService.Responses.InventoryResponse;

public interface InventoryObserver {
    void onSuccessInventory(InventoryResponse succesResponse);
    void onErrorInventory(InventoryResponse errorResponse);
}
