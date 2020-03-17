package com.psych.game.repositories;

import com.psych.game.model.EllenAnswer;
import com.psych.game.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EllenAnswerRepository extends JpaRepository<EllenAnswer, Long> {
    @Query(value = "SELECT * FROM ellenanswers where question_id =:questionId ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    EllenAnswer getRandomAnswer(Long questionId);
}
