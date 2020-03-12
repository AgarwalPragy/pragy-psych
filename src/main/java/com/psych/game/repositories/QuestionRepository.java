package com.psych.game.repositories;

import com.psych.game.model.GameMode;
import com.psych.game.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // jpa provides its own query language - abstracts out the differences
    // JPA - ORM, so we can code DB stuff in java
    // abstracts out the differences b/w DB backends
    // todo
    @Query(value = "SELECT * FROM questions WHERE gameMode=:gameMode ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Question getRandomQuestion(GameMode gameMode);
}
