package ao.ai.cfr.user;


enum GamePhase
{
    NOT_STARTED,
    STARTED,

    USER_TO_ACT,
    USER_THINKING,
    SHOWING_USER_ACTION,

    BOT_TO_ACT,
    BOT_THINKING,
    SHOWING_BOT_ACTION,

    TERMINAL;


    public static GamePhase toAct(boolean userIsNextToAct) {
        return userIsNextToAct ? USER_TO_ACT : BOT_TO_ACT;
    }
}
