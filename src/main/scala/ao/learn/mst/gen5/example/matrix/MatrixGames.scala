package ao.learn.mst.gen5.example.matrix

/**
 * 17/09/13 8:23 PM
 */
object MatrixGames
{
  //--------------------------------------------------------------------------------------------------------------------
  // Anti-coordination games

  // http://en.wikipedia.org/wiki/Game_of_chicken
  val chicken : MatrixGame =
    chicken(0, 2, 6, 7)

//    fromMatrix(
//      Seq((0, 0), (7, 2)),
//      Seq((2, 7), (6, 6)))

  def chicken(crash: Double, lose: Double, tie: Double, win: Double) : MatrixGame = {
    assert(win > tie && tie > lose && lose > crash)
    //lose > crash && crash > win && win > tie)

    fromMatrix(
      Seq((tie, tie ), (lose , win)),
      Seq((win, lose), (crash, crash)))
  }


  //--------------------------------------------------------------------------------------------------------------------
  // http://en.wikipedia.org/wiki/Coordination_game

  def coordination(A: Double, a: Double, B: Double, b: Double, C:Double, c:Double, D:Double, d:Double) : MatrixGame = {
    assert(A > B && D > C)
    assert(a > c && d > b)

    fromMatrix(
      Seq((A, a), (C, c)),
      Seq((B, b), (D, d)))
  }


  // http://en.wikipedia.org/wiki/Battle_of_the_sexes_(game_theory)
  // http://www.gametheory.net/dictionary/Games/BattleoftheSexes.html
  val battleOfTheSexes : MatrixGame =
    coordination(1, 2, 0, 0, 0, 0, 2, 1)


  // http://en.wikipedia.org/wiki/Stag_hunt
  val stagHunt : MatrixGame =
    stagHunt(2, 2, 1, 1, 0, 0, 1, 1)

  def stagHunt(A: Double, a: Double, B: Double, b: Double, C: Double, c: Double, D: Double, d: Double): MatrixGame = {
    assert(a > b && b >= d && d > c)
    assert(A > B && B >= D && D > C)

    coordination(A, a, B, c, C, b, D, d)
  }


  val choosingSides : MatrixGame =
    coordination(10, 10, 0, 0, 0, 0, 10, 10)

  val pureCoordination : MatrixGame =
    coordination(10, 10, 0, 0, 0, 0, 5, 5)



  //--------------------------------------------------------------------------------------------------------------------
  // http://en.wikipedia.org/wiki/Zero%E2%80%93sum_game

//  Quote:
//  *  Red should choose action 1 with probability 4/7 and action 2 with probability 3/7,
//  *  while Blue should assign the probabilities 0, 4/7, and 3/7 to the three actions A, B, and C.
//  *  Red will then win 20/7 points on average per game.

  val zeroSum : MatrixGame =
    fromMatrix(
      Seq((30, -30), (-10,  10), ( 20, -20)),
      Seq((10, -10), ( 20, -20), (-20,  20)))



  //--------------------------------------------------------------------------------------------------------------------
  // http://en.wikipedia.org/wiki/Deadlock_(game)

  val deadlock : MatrixGame =
    deadlock(1, 1, 0, 3, 3, 0, 2, 2)


  def deadlock(
      a : Double, b : Double, c : Double, d : Double,
      e : Double, f : Double, g : Double, h : Double) : MatrixGame =
  {
    assert(e > g && g > a && a > c)
    assert(d > h && h > b && b > f)

    fromMatrix(
      Seq((a, b), (c, d)),
      Seq((e, f), (g, h)))
  }


  //--------------------------------------------------------------------------------------------------------------------
  // http://en.wikipedia.org/wiki/Matching_pennies

  val matchingPennies : MatrixGame = fromMatrix(
    Seq((+1, -1), (-1, +1)),
    Seq((-1, +1), (+1, -1)))


  //--------------------------------------------------------------------------------------------------------------------
  // http://en.wikipedia.org/wiki/Prisoner%27s_dilemma

  val prisonersDilemma : MatrixGame =
    prisonersDilemma(0, -1, -2, -3)

  def prisonersDilemma(t : Double, r : Double, p : Double, s : Double) : MatrixGame = {
    assert(t > r && r > p && p > s)

    fromMatrix(
      Seq((r, r), (s, t)),
      Seq((t, s), (p, p))
    )
  }


  //--------------------------------------------------------------------------------------------------------------------
  def fromMatrix(row : Seq[(Double, Double)]*) : MatrixGame =
    new MatrixGame(
      PayoffMatrixSource(
        row.toSeq
      ))
}
