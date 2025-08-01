package com.br.mefinance.services.exceptions;

public class BusinessException extends  RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}
