package ao.ai.cfr.user.tournament.impl.immutable;


import ao.ai.cfr.user.tournament.TournamentSequence;
import ao.ai.cfr.user.tournament.impl.LiteralInitialUserTournament;


public class ImmutableUserTournament<GameState, TournamentState extends ImmutableTournamentStateTemplate<GameState, TournamentState>>
        extends LiteralInitialUserTournament<TournamentState, GameState>
{
    public ImmutableUserTournament(TournamentState initialTournamentState) {
        super(initialTournamentState);
    }


    @Override
    public int userPlayer(TournamentState tournamentState) {
        return tournamentState.userPlayer();
    }

    @Override
    public TournamentState transition(TournamentState tournamentState, GameState terminalState, double[] payoffs) {
        return tournamentState.transition(terminalState, payoffs);
    }


    @Override
    public TournamentSequence<TournamentState, GameState> newTournamentSequence() {
        return new CopyingTournamentSequence<TournamentState, GameState>(this);
    }
}
