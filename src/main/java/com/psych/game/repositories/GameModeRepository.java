package com.psych.game.repositories;

import com.psych.game.model.GameMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameModeRepository extends JpaRepository<GameMode, Long> {
    Optional<GameMode> findByName(String gameMode);
}
