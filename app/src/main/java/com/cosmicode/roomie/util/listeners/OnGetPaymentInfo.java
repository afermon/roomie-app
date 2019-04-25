package com.cosmicode.roomie.util.listeners;

import com.cosmicode.roomie.domain.Roomie;

public interface OnGetPaymentInfo {

    void onGetPaymentSuccess(Double amount);

    void onGetPaymentError(String error);
}
