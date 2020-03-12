package com.psych.game.controller;

import com.psych.game.exceptions.InvalidGameActionException;
import com.psych.game.model.Player;
import com.psych.game.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/play")
public class GamePlayController {
    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/")
    public Player play(Authentication authentication) {
        return getCurrentPlayer(authentication);
    }

    // GET param: /something?param=value
    // POST param: requestBody
    // URL param: /something/value/
    // pragy-psych.heroku-app.com/play/submit-answer/skdgkjbgkjgb kjsbgk jbg

    @GetMapping("/submit-answer/{answer}")
    public void submitAnswer(Authentication authentication, @PathVariable(name = "answer") String answer) throws InvalidGameActionException {
        Player player = getCurrentPlayer(authentication);
        player.getCurrentGame().submitAnswer(player, answer);
    }

    private Player getCurrentPlayer(Authentication authentication) {
        return playerRepository.findByEmail(authentication.getName()).orElseThrow();
    }
}
