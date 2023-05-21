package com.pandev.currencyratebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CurrencyModel {
    Integer currencyId;
    LocalDateTime currencyDate;
    String currencyAbbreviation;
    Integer currencyScale;
    String currencyName;
    Double currencyOfficialRate;
}
