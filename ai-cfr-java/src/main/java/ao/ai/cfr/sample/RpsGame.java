package ao.ai.cfr.sample;

import ao.ai.cfr.base.AbstractMutableGame;
import ao.ai.cfr.base.MutableStateTemplate;
import ao.ai.cfr.node.Decision;
import ao.ai.cfr.node.ExtensiveNode;
import ao.ai.cfr.node.Terminal;
import ao.ai.cfr.node.choice.ChoiceSet;
import ao.ai.cfr.node.choice.CollectionChoiceSet;
import ao.ai.cfr.user.tournament.UserTournament;
import ao.ai.cfr.user.tournament.impl.mutable.MutableTournamentStateTemplate;
import ao.ai.cfr.user.tournament.impl.mutable.MutableUserTournament;

import java.util.EnumSet;

public class RpsGame
        extends AbstractMutableGame<Void, RpsGame.Action, RpsGame.State>
{
    public RpsGame() {
        super(2, new State());
    }


    @Override
    protected State copy(State state) {
        return new State(state);
    }


    public enum Action {
        ROCK, PAPER, SCISSORS;

        public static final ChoiceSet<Action> CHOICES =
                new CollectionChoiceSet<Action>(EnumSet.allOf(Action.class));

        public double payoff(Action versus) {
            if (this == versus) {
                return 0;
            }

            boolean isWin =
                    (this == ROCK && versus == SCISSORS) ||
                    (this == PAPER && versus == ROCK) ||
                    (this == SCISSORS && versus == PAPER);

            return isWin ? 1 : 0;
        }
    }


    public static class State implements MutableStateTemplate<Void, Action> {
        Action first;
        Action second;

        State() {}

        State(State copy) {
            this.first = copy.first;
            this.second = copy.second;
        }

        @Override
        public ExtensiveNode<Void, Action> node() {
            if (first == null) {
                return Decision.create(0, null, Action.CHOICES);
            }

            if (second == null) {
                return Decision.create(1, null, Action.CHOICES);
            }

            return Terminal.create(first.payoff(second), second.payoff(first));
        }

        @Override
        public void transition(Action action) {
            if (first == null) {
                first = action;
            } else if (second == null) {
                second = action;
            }
        }
    }


    public static final UserTournament<TournamentState, State> TOURNAMENT =
            new MutableUserTournament<State, TournamentState>(
                    new TournamentState());


    public static class TournamentState implements MutableTournamentStateTemplate<State, TournamentState> {
        public int firstScore;
        public int secondScore;

        TournamentState() {}

        TournamentState(TournamentState copy) {
            this.firstScore = copy.firstScore;
            this.secondScore = copy.secondScore;
        }

        @Override
        public void transition(State terminalState, double[] payoffs) {
            firstScore += payoffs[0];
            secondScore += payoffs[1];
        }

        @Override
        public TournamentState copy() {
            return new TournamentState(this);
        }

        @Override
        public int userPlayer() {
            return 0;
        }

        public int userScore() {
            return firstScore;
        }
        public int botScore() {
            return secondScore;
        }
    }
}
