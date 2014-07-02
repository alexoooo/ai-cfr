package ao.ai.cfr.base;


import ao.ai.cfr.ExtensiveGame;
import ao.ai.cfr.ExtensiveMatch;

public abstract class AbstractGame<S, I, A> implements ExtensiveGame<S, I, A>
{
    private final int playerCount;
    private final S initialState;


    public AbstractGame(int playerCount, S initialState) {
        this.playerCount = playerCount;
        this.initialState = initialState;
    }


    @Override
    public final int playerCount() {
        return playerCount;
    }

    @Override
    public final S initialState() {
        return initialState;
    }

    @Override
    public ExtensiveMatch<S, I, A> newMatch() {
        return new GameMatch<S, I, A>(this);
    }
}
