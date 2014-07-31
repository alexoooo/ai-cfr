package ao.ai.cfr.user;


import ao.ai.cfr.ExtensiveGame;
import ao.ai.cfr.ExtensiveMatch;
import ao.ai.cfr.ExtensivePlayer;
import ao.ai.cfr.node.Decision;
import ao.ai.cfr.node.NodeType;
import ao.ai.cfr.user.tournament.TournamentSequence;
import ao.ai.cfr.user.tournament.UserTournament;


public class GameController<TournamentState, GameState, InformationSet, Action>
        implements GameViewOutput<Action>
{
    private final UserTournament<TournamentState, GameState> tournament;
    private final ExtensiveGame<GameState, InformationSet, Action> game;
    private final ExtensivePlayer<InformationSet, Action> bot;
    private final GameView<TournamentState, GameState, InformationSet, Action> view;


    private ExtensiveMatch<GameState, InformationSet, Action> match;
    private TournamentSequence<TournamentState, GameState> tournamentSequence;
    private GamePhase phase;


    public GameController(
            UserTournament<TournamentState, GameState> tournament,
            ExtensiveGame<GameState, InformationSet, Action> game,
            ExtensivePlayer<InformationSet, Action> bot,
            GameView<TournamentState, GameState, InformationSet, Action> view)
    {
        this.tournament = tournament;
        this.game = game;
        this.bot = bot;
        this.view = view;

        tournamentSequence = tournament.newTournamentSequence();
        phase = GamePhase.NOT_STARTED;
    }


    @Override
    public void start() {
        checkPhase(GamePhase.NOT_STARTED);

        match = game.newMatch();
        phase = GamePhase.STARTED;

        TournamentState tournamentState = tournamentSequence.tournamentState();
        view.matchStarted(tournamentState);
    }


    @Override
    public void stateShown() {
        if (phase == GamePhase.STARTED) {
            advanceNode();
        } else if (phase == GamePhase.USER_TO_ACT) {
            phase = GamePhase.USER_THINKING;
        } else if (phase == GamePhase.SHOWING_USER_ACTION) {
            advanceNode();
        } else if (phase == GamePhase.BOT_TO_ACT) {
            botToAct();
        } else if (phase == GamePhase.SHOWING_BOT_ACTION) {
            advanceNode();
        } else if (phase == GamePhase.TERMINAL) {
            advanceMatch();
        } else {
            throw new Error("Unknown phase: " + phase);
        }
    }

    private void botToAct() {
        phase = GamePhase.BOT_THINKING;

        Decision<InformationSet, Action> decision = match.node().asDecision();
        Action action = bot.act(decision.informationSet(), decision.actions());

        botActed(action);
    }

    private void botActed(Action action) {
        match.transition(action);

        phase = GamePhase.SHOWING_BOT_ACTION;

        view.botActed();
    }


    @Override
    public void userActed(Action action) {
        checkPhase(GamePhase.USER_THINKING);

        match.transition(action);

        phase = GamePhase.SHOWING_BOT_ACTION;

        view.userActed();
    }


    private void advanceMatch() {
        double[] payoffs = match.node().asTerminal().payoffs();
        tournamentSequence.transition(match.state(), payoffs);

        phase = GamePhase.NOT_STARTED;

        start();
    }

    private void advanceNode() {
        int userPlayer = tournament.userPlayer(tournamentSequence.tournamentState());

        if (match.node().type() == NodeType.DECISION) {
            if (match.node().asDecision().player() == userPlayer) {
                phase = GamePhase.USER_TO_ACT;
                view.userToAct();
            } else {
                phase = GamePhase.BOT_TO_ACT;
                view.botToAct();
            }
        } else if (match.node().type() == NodeType.TERMINAL) {
            phase = GamePhase.TERMINAL;

            double userPayoff = match.node().asTerminal().payoff(userPlayer);

            view.matchEnded(userPayoff);
        }
    }

    private void checkPhase(GamePhase phaseEquals) {
        if (phase != phaseEquals) {
            throw new IllegalStateException(String.format(
                    "Expected %s but was %s", phaseEquals, phase));
        }
    }
}
