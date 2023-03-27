package com.example.dividend.exception.impl;

import com.example.dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AleradyExsitsUserException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 사용자 입니다.";
    }
}
