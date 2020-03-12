package com.psych.game.repositories;

import com.psych.game.model.EllenAnswer;
import com.psych.game.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EllenAnswerRepository extends JpaRepository<EllenAnswer, Long> {
    // todo
    @Query(value = "SELECT * FROM ellenanswers where question = :question ORDER BY RAND() LIMIT 1", nativeQuery = true)
    EllenAnswer getRandomAnswer(Question question);
}
