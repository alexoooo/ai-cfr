package ao.ai.cfr.user;


import ao.ai.cfr.ExtensiveGame;
import ao.ai.cfr.ExtensiveMatch;
import ao.ai.cfr.ExtensivePlayer;
import ao.ai.cfr.node.Decision;
import ao.ai.cfr.node.NodeType;
import ao.ai.cfr.user.tournament.UserTournament;


public class GameController<TournamentState, GameState, InformationSet, Action>
        implements GameViewOutput<Action>
{
    private final UserTournament<TournamentState, GameState> tournament;
    private final ExtensiveGame<GameState, InformationSet, Action> game;
    private final ExtensivePlayer<InformationSet, Action> bot;
    private final GameView view;


    private ExtensiveMatch<GameState, InformationSet, Action> match;
    private TournamentState tournamentState;
    private GamePhase phase;


    public GameController(
            UserTournament<TournamentState, GameState> tournament,
            ExtensiveGame<GameState, InformationSet, Action> game,
            ExtensivePlayer<InformationSet, Action> bot,
            GameView view)
    {
        this.tournament = tournament;
        this.game = game;
        this.bot = bot;
        this.view = view;

        phase = GamePhase.NOT_STARTED;
    }


    @Override
    public void start() {
        checkPhase(GamePhase.NOT_STARTED);

        match = game.newMatch();
        tournamentState = tournament.initialTournamentState();
        phase = GamePhase.STARTED;

        view.matchStarted();
    }


    @Override
    public void stateShown() {
        if (phase == GamePhase.STARTED) {
            advance();
        } else if (phase == GamePhase.USER_TO_ACT) {
            phase = GamePhase.USER_ACTING;
        } else if (phase == GamePhase.BOT_TO_ACT) {
            phase = GamePhase.BOT_ACTING;

            Decision<InformationSet, Action> decision = match.node().asDecision();
            Action action = bot.act(decision.informationSet(), decision.actions());

            transition(action);
        }
    }


    @Override
    public void userActed(Action action) {
        checkPhase(GamePhase.USER_ACTING);

        transition(action);
    }


    private void transition(Action action) {
        match.transition(action);

        advance();
    }

    private void advance() {
        if (match.node().type() == NodeType.DECISION) {
            if (match.node().asDecision().player() == tournament.userPlayer(tournamentState)) {
                phase = GamePhase.USER_TO_ACT;
                view.userToAct();
            } else {
                phase = GamePhase.BOT_TO_ACT;
                view.botToAct();
            }
        }
    }

    private void checkPhase(GamePhase phaseEquals) {
        if (phase != phaseEquals) {
            throw new IllegalStateException(String.format(
                    "Expected %s but was %s", phaseEquals, phase));
        }
    }
}
