window.app = new Vue({
    el: '#app',
    data: {
        page: "play",
        player: {
            alias: "",
            picURL: "",
            psychFaceURL: "",
            email: "",
            currentGameId: null
        },
        gameModes: [
            {
                title: "",
                image: "",
                description: "",
            },
            {
                title: "",
                image: "",
                description: "",
            },
            {
                title: "",
                image: "",
                description: "",
            },
            {
                title: "",
                image: "",
                description: "",
            },
        ],
        leaderboard: [
            {
                alias: "",
                picURL: "",
                correctAnswerCount: 0,
                gotPsychedCount: 0,
                psychedOthersCount: 0,
            }
        ],
        gameState: {
            id: null,
            secretCode: "",
            numRounds: 10,
            gameMode: "",
            hasEllen: false,
            status: "",
            round: "",
        },
        createGameData: {
            hasEllen: false,
            numRounds: 10,
        },
        joinGameData: {
            secretCode: "",
        },
        profileEditData: {
            alias: "",
            picURL: "",
            psychFaceURL: "",
            email: "",
        },
        errorText: "",
    },
    methods: {
        fetchGameModes: function() {
            fetch('/play/game-modes')
                .then(response => response.json())
                .then(gameModes => {
                    this.gameModes = gameModes;
                });
        },
        fetchPlayerData: function() {
            fetch('/play/player-data')
                .then(response => response.json())
                .then(playerData => {
                    this.player = playerData;
                    this.profileEditData.psychFaceURL = playerData.psychFaceURL;
                    this.profileEditData.alias = playerData.alias;
                    this.profileEditData.picURL = playerData.picURL;
                    this.profileEditData.email = playerData.email;
                });
        },
        fetchGameSate: function() {
            fetch('/play/game-state')
                .then(response => response.json())
                .then(gameState => {
                    this.gameState = gameState;
                });
        },
        fetchLeaderboard: function() {
            fetch('/play/leaderboard')
                .then(response => response.json())
                .then(leaderboard => {
                    this.leaderboard = leaderboard;
                });
        },
        setError: function(response) {
            if(response.status==='success') return;
            this.errorText = "Error: " + response.errorText;
            let self = this;
            setTimeout(() => {self.errorText = '';}, 2000);
        },
        updateProfileData: function() {
            fetch('/play/update-profile?' + new URLSearchParams({
                alias: this.profileEditData.alias,
                email: this.profileEditData.email,
                picURL: this.profileEditData.picURL,
                psychFaceURL: this.profileEditData.psychFaceURL,
            })).then(this.setError).then(this.fetchPlayerData);
        },
        calculateScore: function(stats) {
            return stats.correctAnswerCount * 2 + stats.psychedOthersCount - stats.gotPsychedCount;
        },
        createGame: function (gameMode) {
            fetch('/play/create-game?' + new URLSearchParams({
                gameMode: gameMode,
                numRounds: this.createGameData.numRounds,
                hasEllen: this.createGameData.hasEllen
            })).then(this.setError).then(this.fetchPlayerData).then(this.fetchGameSate);
        },
        joinGame: function (gameMode) {
            fetch('/play/join-game?' + new URLSearchParams({
                secretCode: this.joinGameData.secretCode,
            })).then(this.setError).then(this.fetchPlayerData).then(this.fetchGameSate);
        },
    },
    mounted: function () {
        this.fetchGameModes();
        this.fetchLeaderboard();
        this.fetchGameSate();
        this.fetchPlayerData();
        document.getElementById("app").classList.remove("invisible");
    }
});
