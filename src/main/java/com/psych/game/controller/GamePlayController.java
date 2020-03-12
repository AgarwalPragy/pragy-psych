package com.psych.game.controller;

import com.psych.game.exceptions.InvalidGameActionException;
import com.psych.game.model.Game;
import com.psych.game.model.Player;
import com.psych.game.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RequestMapping("/play")
public class GamePlayController {
    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/")
    public String play(Authentication authentication) {
        return authentication.getName();
    }

    // GET param: /something?param=value
    // POST param: requestBody
    // URL param: /something/value/
    // pragy-psych.heroku-app.com/play/submit-answer/skdgkjbgkjgb kjsbgk jbg

    @GetMapping("/submit-answer/{answer}")
    public void submitAnswer(Authentication authentication, @PathVariable(name="answer") String answer) throws InvalidGameActionException {
        Player player = playerRepository.findByEmail(authentication.getName()).orElseThrow();
        player.getCurrentGame().submitAnswer(player, answer);
    }
}
