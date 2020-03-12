package com.psych.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.psych.game.Utils;
import com.psych.game.exceptions.InvalidGameActionException;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "games")
public class Game extends Auditable {
    @ManyToMany
    @JsonIdentityReference
    @Getter
    @Setter
    private Set<Player> players = new HashSet<>();

    @ManyToOne
    @JsonIdentityReference
    @Getter
    @Setter
    @NotNull
    private GameMode gameMode;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @OrderBy(value = "round_number asc")
    @JsonManagedReference
    @Getter
    @Setter
    private List<Round> rounds = new ArrayList<>();

    @Getter
    @Setter
    private int numRounds = 10;

    @Getter
    @Setter
    private Boolean hasEllen = false;

    @NotNull
    @JsonIdentityReference
    @Getter
    @Setter
    @ManyToOne
    private Player leader;

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
    @Getter
    @Setter
    private Map<Player, Stat> playerStats = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private GameStatus gameStatus = GameStatus.PLAYERS_JOINING;

    @ManyToMany
    @JsonIdentityReference
    @Getter
    @Setter
    private Set<Player> readyPlayers = new HashSet<>();

    public Game() {
    }

    public Game(@NotNull GameMode gameMode, int numRounds, Boolean hasEllen, @NotNull Player leader) {
        this.gameMode = gameMode;
        this.numRounds = numRounds;
        this.hasEllen = hasEllen;
        this.leader = leader;
        try {
            addPlayer(leader);
        } catch (InvalidGameActionException ignored) {
        }
    }

    public void addPlayer(Player player) throws InvalidGameActionException {
        if (!gameStatus.equals(GameStatus.PLAYERS_JOINING))
            throw new InvalidGameActionException("Can't join after the game has started");
        players.add(player);
        player.setCurrentGame(this);
    }

    public void removePlayer(Player player) throws InvalidGameActionException {
        if (!players.contains(player))
            throw new InvalidGameActionException("No such player was in the game.");
        players.remove(player);
        if (player.getCurrentGame().equals(this))
            player.setCurrentGame(null);
        if (players.size() == 0 || (players.size() == 1 && !gameStatus.equals(GameStatus.PLAYERS_JOINING)))
            endGame();
    }

    public void startGame(Player player) throws InvalidGameActionException {
        if (!gameStatus.equals(GameStatus.PLAYERS_JOINING))
            throw new InvalidGameActionException("The game has already started");
        if (players.size() < 2)
            throw new InvalidGameActionException("Can't start a game with a single player");
        if (!player.equals(leader))
            throw new InvalidGameActionException("Only the leader can start the game");
        startNewRound();
    }

    private void startNewRound() {
        gameStatus = GameStatus.SUBMITTING_ANSWERS;
        Question question = Utils.getRandomQuestion(gameMode);
        Round round = new Round(this, question, rounds.size() + 1);
        if (hasEllen)
            round.setEllenAnswer(Utils.getRandomEllenAnswer(question));
        rounds.add(new Round());
    }

    public void submitAnswer(Player player, String answer) throws InvalidGameActionException {
        if (answer.length() == 0)
            throw new InvalidGameActionException("Answer cannot be empty");
        if (!players.contains(player))
            throw new InvalidGameActionException("No such player was in the game.");
        if (!gameStatus.equals(GameStatus.SUBMITTING_ANSWERS))
            throw new InvalidGameActionException("Game is not accepting answers at present");
        Round currentRound = getCurrentRound();
        currentRound.submitAnswer(player, answer);
        if (currentRound.allAnswersSubmitted(players.size()))
            gameStatus = GameStatus.SELECTING_ANSWERS;
    }

    public void selectAnswer(Player player, PlayerAnswer selectedAnswer) throws InvalidGameActionException {
        if (!players.contains(player))
            throw new InvalidGameActionException("No such player was in the game.");
        if (!gameStatus.equals(GameStatus.SELECTING_ANSWERS))
            throw new InvalidGameActionException("Game is not selecting answers at present");
        Round currentRound = getCurrentRound();
        currentRound.selectAnswer(player, selectedAnswer);

        if (currentRound.allAnswersSelected(players.size())) {
            if (rounds.size() < numRounds)
                gameStatus = GameStatus.WAITING_FOR_READY;
            else
                endGame();
        }
    }

    public void playerIsReady(Player player) throws InvalidGameActionException {
        if (!players.contains(player))
            throw new InvalidGameActionException("No such player was in the game.");
        if (!gameStatus.equals(GameStatus.WAITING_FOR_READY))
            throw new InvalidGameActionException("Game is not waiting for players to be ready");
        readyPlayers.add(player);
        if (readyPlayers.size() == players.size())
            startNewRound();
    }

    public void playerIsNotReady(Player player) throws InvalidGameActionException {
        if (!players.contains(player))
            throw new InvalidGameActionException("No such player was in the game.");
        if (!gameStatus.equals(GameStatus.WAITING_FOR_READY))
            throw new InvalidGameActionException("Game is not waiting for players to be ready");
        readyPlayers.remove(player);
    }


    private Round getCurrentRound() throws InvalidGameActionException {
        if (rounds.size() == 0)
            throw new InvalidGameActionException("The game has not started");
        return rounds.get(rounds.size() - 1);
    }

    private void endGame() {
        gameStatus = GameStatus.ENDED;
        for (Player player : players)
            if (player.getCurrentGame().equals(this))
                player.setCurrentGame(null);
    }

    public JSONObject getGameState() {
        JSONObject state = new JSONObject();
        state.put("id", getId());
        state.put("numRounds", numRounds);
        state.put("mode", gameMode.getName());
        JSONArray playerData = new JSONArray();
        for(Player player: players) {
            JSONObject data = new JSONObject();
            data.put("alias", player.getAlias());
            playerData.add(data);
        }
        state.put("players", playerData);
        return state;
    }

}
