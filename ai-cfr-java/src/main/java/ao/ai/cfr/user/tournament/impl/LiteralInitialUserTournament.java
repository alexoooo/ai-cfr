package ao.ai.cfr.user.tournament.impl;


import ao.ai.cfr.user.tournament.UserTournament;

public abstract class LiteralInitialUserTournament<TournamentState, GameState>
        implements UserTournament<TournamentState, GameState>
{
    private final TournamentState initialTournamentState;


    public LiteralInitialUserTournament(TournamentState initialTournamentState) {
        this.initialTournamentState = initialTournamentState;
    }


    @Override
    public TournamentState initialTournamentState() {
        return initialTournamentState;
    }
}
