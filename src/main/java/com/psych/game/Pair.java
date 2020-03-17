package com.psych.game;

import lombok.Getter;
import lombok.Setter;

public class Pair<T, T1> {
    @Getter
    @Setter
    T first;
    @Getter
    @Setter
    T1 second;

    public Pair(T first, T1 second) {
        this.first = first;
        this.second = second;
    }
}
