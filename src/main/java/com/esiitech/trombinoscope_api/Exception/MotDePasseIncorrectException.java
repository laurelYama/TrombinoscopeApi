package com.esiitech.trombinoscope_api.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MotDePasseIncorrectException extends ResponseStatusException {
    public MotDePasseIncorrectException() {
        super(HttpStatus.BAD_REQUEST, "Ancien mot de passe incorrect");
    }
}
