package com.stellmangreene.pbprdf.plays

import org.openrdf.model.Resource
import org.openrdf.model.URI
import org.openrdf.model.Value
import org.openrdf.model.vocabulary.RDF
import org.openrdf.repository.Repository
import com.stellmangreene.pbprdf.model.EntityUriFactory
import com.stellmangreene.pbprdf.model.Ontology
import com.stellmangreene.pbprdf.util.RdfOperations
import com.typesafe.scalalogging.LazyLogging
import com.stellmangreene.pbprdf.GamePeriodInfo

/**
 * A play that represents the end of a period or game
 * <p>
 * Examples:
 * End of the 1st Quarter
 * End of Game
 *
 * @param gameID
 *        Unique ID of the game
 * @param eventNumber
 *        Sequential number of this event
 * @param period
 *        Period this occurred in (overtime starts with period 5)
 * @param team
 *        Name of the team
 * @param play
 *        Description of the play (eg. "Alyssa Thomas makes free throw 2 of 2")
 * @param score
 *        Game score ("10-4") - CURRENTLY IGNORED
 *
 * @author andrewstellman
 */
class EndOfPlay(gameUri: URI, eventNumber: Int, period: Int, time: String, team: String, play: String, score: String, gamePeriodInfo: GamePeriodInfo)
  extends Play(gameUri: URI, eventNumber: Int, period: Int, time: String, team: String, play: String, score: String, gamePeriodInfo: GamePeriodInfo)
  with RdfOperations
  with LazyLogging {

  override def addRdf(rep: Repository) = {
    logger.debug(s"Parsing timeout from play: ${play}")

    val triples: Set[(Resource, URI, Value)] =
      play match {
        case EndOfPlay.playByPlayRegex("Game") => {
          Set(
            (eventUri, RDF.TYPE, Ontology.END_OF_GAME))
        }
        case _ => {
          Set(
            (eventUri, RDF.TYPE, Ontology.END_OF_PERIOD))
        }
      }

    if (!triples.isEmpty)
      rep.addTriples(triples)

    super.addRdf(rep)
  }

}

/**
 * Companion object that defines a regex that can be used to check this play against a description
 */
object EndOfPlay extends PlayMatcher {

  val playByPlayRegex = """^End of (.+)$""".r

}