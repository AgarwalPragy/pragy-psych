package com.psych.game;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int MAX_QUESTIONS_TO_READ = 20;
    public static Map<String, String> QA_FILES;

    static {
        QA_FILES = new HashMap<>();
        QA_FILES.put("classpath:data/qa_facts.txt", "Is This A Fact?");
        QA_FILES.put("classpath:data/qa_unscramble.txt", "Un-Scramble");
        QA_FILES.put("classpath:data/qa_word_up.txt", "Word Up");
    }
}
