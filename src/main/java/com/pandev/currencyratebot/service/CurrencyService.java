package com.pandev.currencyratebot.service;

import com.pandev.currencyratebot.model.CurrencyModel;

public interface CurrencyService {

     CurrencyModel getCurrency(String message);

     Double getUsdToKztRate();

     String currencyExchange(String takenValue);
}
