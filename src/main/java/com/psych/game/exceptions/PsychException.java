package com.psych.game.exceptions;

import lombok.Getter;
import lombok.Setter;

public class PsychException extends Exception {
    @Getter
    @Setter
    private String message;

    public PsychException(String message) {
        super();
        this.message = message;
    }
}
