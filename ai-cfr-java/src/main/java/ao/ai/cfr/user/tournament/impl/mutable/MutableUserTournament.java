package ao.ai.cfr.user.tournament.impl.mutable;


import ao.ai.cfr.user.tournament.TournamentSequence;
import ao.ai.cfr.user.tournament.impl.LiteralInitialUserTournament;

public class MutableUserTournament<GameState, TournamentState extends MutableTournamentStateTemplate<GameState, TournamentState>>
        extends LiteralInitialUserTournament<TournamentState, GameState>
{
    public MutableUserTournament(TournamentState initialTournamentState) {
        super(initialTournamentState);
    }


    @Override
    public int userPlayer(TournamentState tournamentState) {
        return tournamentState.userPlayer();
    }

    @Override
    public TournamentState transition(TournamentState tournamentState, GameState terminalState, double[] payoffs) {
        TournamentState prototype = tournamentState.copy();
        prototype.transition(terminalState, payoffs);
        return prototype;
    }

    @Override
    public TournamentSequence<TournamentState, GameState> newTournamentSequence() {
        return new InPlaceTournamentSequence<GameState, TournamentState>(initialTournamentState());
    }
}
