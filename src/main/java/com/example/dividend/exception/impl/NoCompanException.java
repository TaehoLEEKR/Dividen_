package com.example.dividend.exception.impl;

import com.example.dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NoCompanException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 회사명 입니다.";
    }
}
