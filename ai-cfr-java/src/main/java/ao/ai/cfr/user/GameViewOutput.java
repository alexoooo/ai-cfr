package ao.ai.cfr.user;


public interface GameViewOutput<Action>
{
    void start();

    void userActed(Action action);

    void stateShown();
}
