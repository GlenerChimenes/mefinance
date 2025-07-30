package com.br.mefinance.utils;

import java.time.LocalDate;

public class DataUtils {

    /**
     * Converte uma data para o formato mmyyyycomo Integer.
     * Ex: 5 de agosto de 2025 -> 082025
     *
     * @param data A data base (LocalDate)
     * @return Um Integer no formato mmyyyy
     */
    public static Integer converterLocalDateParaPerido(LocalDate data){
        int mes = data.getMonthValue();
        int ano = data.getYear();
        return mes * 10000 + ano;
    }
}
