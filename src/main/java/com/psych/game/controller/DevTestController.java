package com.psych.game.controller;

import com.psych.game.Constants;
import com.psych.game.Pair;
import com.psych.game.Utils;
import com.psych.game.exceptions.InvalidGameActionException;
import com.psych.game.model.*;
import com.psych.game.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dev-test")
public class DevTestController {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameModeRepository gameModeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private ContentWriterRepository contentWriterRepository;
    @Autowired
    private EllenAnswerRepository ellenAnswerRepository;

    @GetMapping("/")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/populate")
    public String populateDB() throws InvalidGameActionException {
        for (Player player : playerRepository.findAll()) {
            player.getGames().clear();
            player.setCurrentGame(null);
            playerRepository.save(player);
        }
        gameRepository.deleteAll();
        playerRepository.deleteAll();
        questionRepository.deleteAll();
        gameModeRepository.deleteAll();

        Player luffy = new Player.Builder()
                .alias("Monkey D. Luffy")
                .email("luffy@psych.com")
                .saltedHashedPassword("strawhat")
                .build();
        playerRepository.save(luffy);
        Player robin = new Player.Builder()
                .alias("Nico Robin")
                .email("robin@psych.com")
                .saltedHashedPassword("poneglyph")
                .build();
        playerRepository.save(robin);

        GameMode isThisAFact = new GameMode("Is This A Fact?", "https://i.pinimg.com/originals/29/cb/75/29cb75e488831ba018fe5f0925b8e39f.png", "is this a fact description");
        gameModeRepository.save(isThisAFact);
        gameModeRepository.save(new GameMode("Word Up", "https://i.pinimg.com/originals/29/cb/75/29cb75e488831ba018fe5f0925b8e39f.png", "word up description"));
        gameModeRepository.save(new GameMode("Un-Scramble", "https://i.pinimg.com/originals/29/cb/75/29cb75e488831ba018fe5f0925b8e39f.png", "unscramble descirption"));
        gameModeRepository.save(new GameMode("Movie Buff", "https://i.pinimg.com/originals/29/cb/75/29cb75e488831ba018fe5f0925b8e39f.png", "movie buff description"));

        List<Question> questions = new ArrayList<>();
        for (Map.Entry<String, String> fileMode : Constants.QA_FILES.entrySet()) {
            GameMode gameMode = gameModeRepository.findByName(fileMode.getValue()).orElseThrow();
            for (Pair<String, String> questionAnswer : Utils.readQAFile(fileMode.getKey())) {
                questions.add(new Question(questionAnswer.getFirst(), questionAnswer.getSecond(), gameMode));
            }
        }
        questionRepository.saveAll(questions);

        Game game = new Game(isThisAFact, 15, true, luffy);
        game.addPlayer(robin);
        gameRepository.save(game);

        game.startGame(luffy);
        gameRepository.save(game);

        return "populated";
    }

    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @GetMapping("/question/{id}")
    public Question getQuestionById(@PathVariable(name = "id") Long id) {
        return questionRepository.findById(id).orElseThrow();
    }

    @GetMapping("/players")
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @GetMapping("/player/{id}")
    public Player getPlayerById(@PathVariable(name = "id") Long id) {
        return playerRepository.findById(id).orElseThrow();
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable(name = "id") Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @GetMapping("/games")
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @GetMapping("/game/{id}")
    public Game getGameById(@PathVariable(name = "id") Long id) {
        return gameRepository.findById(id).orElseThrow();
    }

    @GetMapping("/admins")
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @GetMapping("/admin/{id}")
    public Admin getAdminById(@PathVariable(name = "id") Long id) {
        return adminRepository.findById(id).orElseThrow();
    }

    @GetMapping("/rounds")
    public List<Round> getAllRounds() {
        return roundRepository.findAll();
    }

    @GetMapping("/round/{id}")
    public Round getRoundById(@PathVariable(name = "id") Long id) {
        return roundRepository.findById(id).orElseThrow();
    }

    @GetMapping("/contentWriters")
    public List<ContentWriter> getAllContentWriters() {
        return contentWriterRepository.findAll();
    }

    @GetMapping("/contentWriter/{id}")
    public ContentWriter getContentWriterById(@PathVariable(name = "id") Long id) {
        return contentWriterRepository.findById(id).orElseThrow();
    }

    @GetMapping("/ellenAnswers")
    public List<EllenAnswer> getAllEllenAnswers() {
        return ellenAnswerRepository.findAll();
    }

    @GetMapping("/ellenAnswer/{id}")
    public EllenAnswer getEllenAnswerById(@PathVariable(name = "id") Long id) {
        return ellenAnswerRepository.findById(id).orElseThrow();
    }
}