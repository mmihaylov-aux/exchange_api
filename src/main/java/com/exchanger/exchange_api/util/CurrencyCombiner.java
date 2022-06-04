package com.exchanger.exchange_api.util;

public class CurrencyCombiner {
    public static String combine(String source, String target){
        return "%s%s".formatted(source, target);
    }
}
