package ao.ai.cfr.user;


enum GamePhase
{
    NOT_STARTED,
    STARTED,

    USER_TO_ACT,
    USER_ACTING,

    BOT_TO_ACT,
    BOT_ACTING,

    TERMINAL;


    public static GamePhase toAct(boolean userIsNextToAct) {
        return userIsNextToAct ? USER_TO_ACT : BOT_TO_ACT;
    }
}
