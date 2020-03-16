package com.psych.game.controller;

import com.psych.game.exceptions.InvalidGameActionException;
import com.psych.game.model.Game;
import com.psych.game.model.GameMode;
import com.psych.game.model.Player;
import com.psych.game.repositories.GameModeRepository;
import com.psych.game.repositories.GameRepository;
import com.psych.game.repositories.PlayerRepository;
import lombok.Getter;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/play")
public class GamePlayAPI {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameModeRepository gameModeRepository;
    @Autowired
    private GameRepository gameRepository;

    private Player getCurrentPlayer(Authentication authentication) {
        return playerRepository.findByEmail(authentication.getName()).orElseThrow();
    }

    private JSONObject getData(Player player) {
        Game currentGame = player.getCurrentGame();
        JSONObject response = new JSONObject();
        response.put("playerAlias", player.getAlias());
        response.put("currentGame", currentGame==null?null:currentGame.getId());
        if(currentGame==null) {
            JSONArray gameModes = new JSONArray();
            for (GameMode mode : gameModeRepository.findAll()) {
                JSONObject gameMode = new JSONObject();
                gameMode.put("title", mode.getName());
                gameMode.put("image", mode.getPicture());
                gameMode.put("descrption", mode.getDescription());
                gameModes.add(gameMode);
            }
            response.put("gameModes", gameModes);
        }
        else {
            response.put("gameState", currentGame.getGameState());
        }
        return response;
    }

    @GetMapping("")
    public JSONObject play(Authentication authentication) {
        Player player = getCurrentPlayer(authentication);
        return getData(player);
    }

    @GetMapping("/create-game")
    public JSONObject createGame(Authentication authentication,
                           @RequestParam(name = "mode") String gameMode,
                           @RequestParam(name = "rounds") Integer numRounds,
                           @RequestParam(name = "ellen") Boolean hasEllen) {
        Player leader = getCurrentPlayer(authentication);
        GameMode mode = gameModeRepository.findByName(gameMode).orElseThrow();
        gameRepository.save(new Game(mode, numRounds, hasEllen, leader));
        return getData(leader);
    }

    @GetMapping("/luffy-submit")
    public String luffySubmit() throws InvalidGameActionException {
        Player luffy = playerRepository.findByEmail("luffy@psych.com").orElseThrow();
        Game game = luffy.getCurrentGame();
        game.submitAnswer(luffy, "answer");
        gameRepository.save(game);
        return "done";
    }

    @GetMapping("/robin-submit")
    public String robinSubmit() throws InvalidGameActionException {
        Player robin = playerRepository.findByEmail("robin@psych.com").orElseThrow();
        Game game = robin.getCurrentGame();
        game.submitAnswer(robin, "answer");
        gameRepository.save(game);
        return "done";
    }
}