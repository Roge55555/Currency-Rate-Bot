package com.pandev.currencyratebot.service.impl;

import com.pandev.currencyratebot.model.CurrencyModel;
import com.pandev.currencyratebot.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    @Override
    public CurrencyModel getCurrency(String message) {
        URL url;
        Scanner scanner = null;
        try {
            url = new URL("https://www.nbrb.by/api/exrates/rates/" + message + "?parammode=2");
            scanner = new Scanner((InputStream) url.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder result = new StringBuilder();
        while (scanner != null && scanner.hasNext()){
            result.append(scanner.nextLine());
        }
        JSONObject object = new JSONObject(result.toString());

        return CurrencyModel.builder()
                .currencyId(object.getInt("Cur_ID"))
                .currencyDate(LocalDateTime.parse(object.getString("Date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .currencyAbbreviation(object.getString("Cur_Abbreviation"))
                .currencyScale(object.getInt("Cur_Scale"))
                .currencyName(object.getString("Cur_Name"))
                .currencyOfficialRate(object.getDouble("Cur_OfficialRate"))
                .build();
    }

    @Override
    public Double getUsdToKztRate() {
        return 1000 / getCurrency("kzt").getCurrencyOfficialRate() * getCurrency("usd").getCurrencyOfficialRate();
    }

    @Override
    public String currencyExchange(String takenValue) {

        if (takenValue.contains("$")) {
            return String.format("%,.2f", Double.parseDouble(takenValue.replace("$", "").replace(",", ".")) * getUsdToKztRate()) + " тенге";
        } else {
            return String.format("%,.2f", Double.parseDouble(takenValue.replace("тенге", "").replace(",", ".")) / getUsdToKztRate()) + "$";
        }
    }
}
