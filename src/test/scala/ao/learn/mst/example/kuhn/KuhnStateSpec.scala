package ao.learn.mst.example.kuhn

import action.{KuhnAction, KuhnActionSequence}
import card.KuhnDeck
import state.terminal.KuhnTerminalStatus
import ao.learn.mst.example.kuhn.state.{KuhnPosition, KuhnState}
import util.Random
import org.specs2.mutable.SpecificationWithJUnit


//----------------------------------------------------------------------------------------------------------------------
/**
 * See: http://en.wikipedia.org/wiki/Kuhn_poker -> "In more conventional poker terms".
 */
class KuhnStateSpec
    extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  "Initial state" should {
    val arbitraryCards =
      new KuhnDeck().deal(new Random())

    val initialState =
      new KuhnState(
        arbitraryCards)

    "No action has been taken" in {
      initialState.actions === KuhnActionSequence.FirstAction
    }

    "First player is next to act" in {
      initialState.nextToAct === Some(KuhnPosition.FirstToAct)
    }

    "Each player antes 1" in {
      initialState.stake.firstPlayer === 1
      initialState.stake.lastPlayer  === 1
    }

    "Each player is dealt one of the three cards, and the third is put aside unseen" in {
      initialState.cards === arbitraryCards
    }

    "First player can check or raise" in {
      initialState.terminalStatus === None

      initialState.availableActions must equalTo(
        Seq(KuhnAction.CheckFold, KuhnAction.CallRaise))
    }


    //------------------------------------------------------------------------------------------------------------------
    "On first player checks" in {
      val onFirstPlayerChecks =
          initialState.act(KuhnAction.CheckFold)

      "Action sequence should match" in {
        onFirstPlayerChecks.actions === KuhnActionSequence.Check
      }

      "Second player is next to act" in {
        onFirstPlayerChecks.nextToAct === Some(KuhnPosition.LastToAct)
      }

      "Each player still antes 1" in {
        onFirstPlayerChecks.stake === initialState.stake
      }

      "Second player can check or raise" in {
        onFirstPlayerChecks.terminalStatus === None

        onFirstPlayerChecks.availableActions ===
          Seq(KuhnAction.CheckFold, KuhnAction.CallRaise)
      }


      //----------------------------------------------------------------------------------------------------------------
      "On second player checks" in {
        val onSecondPlayerChecks =
            onFirstPlayerChecks.act(KuhnAction.CheckFold)

        "Action sequence should match" in {
          onSecondPlayerChecks.actions === KuhnActionSequence.CheckCheck
        }

        "There are no more actions available" in {
          onSecondPlayerChecks.nextToAct === None

          onSecondPlayerChecks.availableActions must be empty
        }

        "There is a showdown for the pot of 2" in {
          onSecondPlayerChecks.terminalStatus ===
            Some(KuhnTerminalStatus.SmallShowdown)

          onSecondPlayerChecks.stake === initialState.stake
        }


        //--------------------------------------------------------------------------------------------------------------
        "On second player raises" in {
          val onSecondPlayerRaises =
            onFirstPlayerChecks.act(KuhnAction.CallRaise)

          "Action sequence should match" in {
            onSecondPlayerRaises.actions === KuhnActionSequence.CheckRaise
          }

          "Stakes have to refelect raise" in {
            onSecondPlayerRaises.stake.firstPlayer === 1
            onSecondPlayerRaises.stake.lastPlayer  === 2
          }

          "Player One can fold or call" in {
            onSecondPlayerRaises.nextToAct === Some(KuhnPosition.FirstToAct)

            onSecondPlayerRaises.availableActions ===
              Seq(KuhnAction.CheckFold, KuhnAction.CallRaise)

            onSecondPlayerRaises.terminalStatus === None
          }


          //------------------------------------------------------------------------------------------------------------
          "On first player folds" in {
            val onFirstPlayerFolds =
              onSecondPlayerRaises.act(KuhnAction.CheckFold)

            "Action sequence matches" in {
              onFirstPlayerFolds.actions === KuhnActionSequence.CheckRaiseFold
            }

            "There are no more actions" in {
              onFirstPlayerFolds.availableActions must be empty
            }

            "Player two takes pot of 3" in {
              onFirstPlayerFolds.terminalStatus === Some(KuhnTerminalStatus.LastToActWins)
              onFirstPlayerFolds.stake.total === 3
            }
          }


          //------------------------------------------------------------------------------------------------------------
          "On first player calls" in {
            val onFirstPlayerCalls =
              onSecondPlayerRaises.act(KuhnAction.CallRaise)

            "Action sequence matches" in {
              onFirstPlayerCalls.actions === KuhnActionSequence.CheckRaiseCall
            }

            "There is a showdown for the pot of 4" in {
              onFirstPlayerCalls.stake.firstPlayer === 2
              onFirstPlayerCalls.stake.lastPlayer  === 2
              onFirstPlayerCalls.stake.total       === 4

              onFirstPlayerCalls.terminalStatus === Some(KuhnTerminalStatus.BigShowdown)
            }
          }
        }
      }
    }


    //------------------------------------------------------------------------------------------------------------------
    "On first player raises" in {
      val onFirstPlayerRaises =
        initialState.act(KuhnAction.CallRaise)

      "Action sequence matches" in {
        onFirstPlayerRaises.actions === KuhnActionSequence.Raise
      }

      "Second player is next to act" in {
        onFirstPlayerRaises.nextToAct === Some(KuhnPosition.LastToAct)
      }

      "Stakes reflect raise" in {
        onFirstPlayerRaises.stake.firstPlayer === 2
        onFirstPlayerRaises.stake.lastPlayer  === 1
      }

      "Second player can fold or call" in {
        onFirstPlayerRaises.terminalStatus === None

        onFirstPlayerRaises.availableActions ===
          Seq(KuhnAction.CheckFold, KuhnAction.CallRaise)
      }


      //----------------------------------------------------------------------------------------------------------------
      "On player two folds" in {
        val onLastPlayerFolds =
          onFirstPlayerRaises.act(KuhnAction.CheckFold)

        "Action sequence matches" in {
          onLastPlayerFolds.actions === KuhnActionSequence.RaiseFold
        }

        "Player One wins" in {
          onLastPlayerFolds.terminalStatus === Some(KuhnTerminalStatus.FirstToActWins)
        }

        "pot size is 3" in {
          onLastPlayerFolds.stake.total === 3
        }

        "There are no more actions" in {
          onLastPlayerFolds.nextToAct === None

          onLastPlayerFolds.availableActions must be empty
        }
      }


      //----------------------------------------------------------------------------------------------------------------
      "On player two calls" in {
        val onLastPlayerCalls =
          onFirstPlayerRaises.act(KuhnAction.CallRaise)

        "Action sequence matches" in {
          onLastPlayerCalls.actions === KuhnActionSequence.RaiseCall
        }

        "There are no more moves" in {
          onLastPlayerCalls.nextToAct === None
          onLastPlayerCalls.availableActions must be empty
        }

        "There is a showdown" in {
          onLastPlayerCalls.terminalStatus === Some(KuhnTerminalStatus.BigShowdown)
        }

        "The pot size is 4" in {
          onLastPlayerCalls.stake.firstPlayer === 2
          onLastPlayerCalls.stake.lastPlayer  === 2
          onLastPlayerCalls.stake.total       === 4
        }
      }
    }
  }
}