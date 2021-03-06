package com.stellmangreene.pbprdf.plays

import org.eclipse.rdf4j.model.Resource
import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.model.Value
import org.eclipse.rdf4j.model.vocabulary.RDF
import org.eclipse.rdf4j.repository.Repository

import com.stellmangreene.pbprdf.GamePeriodInfo
import com.stellmangreene.pbprdf.model.EntityIriFactory
import com.stellmangreene.pbprdf.model.Ontology
import com.typesafe.scalalogging.LazyLogging

import com.stellmangreene.pbprdf.util.RdfOperations._

/**
 * A play that represents a shot
 * <p>
 * Examples:
 * Kelsey Bone  misses jumper
 * Stefanie Dolson  misses 13-foot jumper
 * Emma Meesseman makes 13-foot two point shot
 * Jasmine Thomas makes layup (Alex Bentley assists)
 * Kara Lawson makes 24-foot  three point jumper  (Ivory Latta assists)
 * Ivory Latta  misses finger roll layup
 * Natasha Cloud misses free throw 1 of 2
 * Alyssa Thomas makes free throw 2 of 2
 * Alex Bentley makes technical free throw
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
class ShotPlay(gameIri: IRI, eventNumber: Int, period: Int, time: String, team: String, play: String, score: String, gamePeriodInfo: GamePeriodInfo)
  extends Play(gameIri: IRI, eventNumber: Int, period: Int, time: String, team: String, play: String, score: String, gamePeriodInfo: GamePeriodInfo)
  with LazyLogging {

  override def addRdf(rep: Repository) = {
    val triples: Set[(Resource, IRI, Value)] =
      play match {
        case ShotPlay.playByPlayRegex(player, makesMisses, shotType, assists) => {
          logger.debug(s"Parsing shot from play: ${play}")

          val made =
            if (makesMisses.trim == "makes")
              true
            else
              false

          val pointsTriple: Set[(Resource, IRI, Value)] =
            if (shotType.contains("free throw"))
              Set((eventIri, Ontology.SHOT_POINTS, rep.getValueFactory.createLiteral(1)))
            else if (shotType.contains("three point"))
              Set((eventIri, Ontology.SHOT_POINTS, rep.getValueFactory.createLiteral(3)))
            else
              Set((eventIri, Ontology.SHOT_POINTS, rep.getValueFactory.createLiteral(2)))

          val assistsRegex = """ *\( *(.*) assists\)""".r

          val assistedByTriple: Set[(Resource, IRI, Value)] =
            assists match {
              case assistsRegex(assistedBy) =>
                Set((eventIri, Ontology.SHOT_ASSISTED_BY, EntityIriFactory.getPlayerIri(assistedBy)))
              case _ => Set()
            }

          val shotTypeTriple: Set[(Resource, IRI, Value)] =
            if (!shotType.trim.isEmpty)
              Set((eventIri, Ontology.SHOT_TYPE, rep.getValueFactory.createLiteral(shotType.trim)))
            else
              Set()

          Set(
            (eventIri, RDF.TYPE, Ontology.SHOT),
            (eventIri, Ontology.SHOT_BY, EntityIriFactory.getPlayerIri(player)),
            (eventIri, Ontology.SHOT_MADE, rep.getValueFactory.createLiteral(made))) ++
            pointsTriple ++
            shotTypeTriple ++
            assistedByTriple
        }

        case _ => { logger.warn(s"Unrecognized shot play: ${play}"); Set() }
      }

    if (!triples.isEmpty)
      rep.addTriples(triples)

    super.addRdf(rep)
  }

}

/**
 * Companion object that defines a regex that can be used to check this play against a description
 */
object ShotPlay extends PlayMatcher {

  val playByPlayRegex = """^(.*) (makes|misses) ?(.*?)( \(.* assists\))?$""".r

}
