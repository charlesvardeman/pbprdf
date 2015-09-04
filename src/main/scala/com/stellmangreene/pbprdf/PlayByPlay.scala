package com.stellmangreene.pbprdf

import org.openrdf.repository.Repository
import com.stellmangreene.pbprdf.model.Ontology
import org.openrdf.model.vocabulary.RDF
import com.stellmangreene.pbprdf.model.EntityUriFactory
import org.openrdf.model.URI
import com.stellmangreene.pbprdf.util.RdfOperations
import com.stellmangreene.pbprdf.plays.EnterPlay
import com.typesafe.scalalogging.LazyLogging
import org.openrdf.model.vocabulary.RDFS
import org.openrdf.model.BNode
import org.joda.time.DateTime

/**
 * Play by play
 *
 * @author andrewstellman
 */
abstract class PlayByPlay extends RdfOperations with LazyLogging {

  /** Events from the play-by-play */
  val events: Seq[Event]

  /** URI of this game */
  val gameUri: URI

  /** Name of the home team */
  val homeTeam: String

  /** Final score for the home team */
  val homeScore: String

  /** Name of the away team */
  val awayTeam: String

  /** Final score for the away team */
  val awayScore: String

  /** Game location */
  val gameLocation: String

  /** Game time */
  val gameTime: DateTime

  /**
   * Add the events to an RDF repository
   *
   * @param rep
   *            Sesame repository to add the events to
   */
  def addRdf(rep: Repository) = {
    rep.addTriple(gameUri, RDF.TYPE, Ontology.GAME, EntityUriFactory.contextUri)
    addRosterBnodes(rep)
  }

  /**
   * Use the "player enters" events to build the home and away team rosters and
   * add a bnode for each roster
   *
   * @param rep
   *            Sesame repository to add the events to
   */
  protected def addRosterBnodes(rep: Repository) = {

    val homeTeamRosterBnode = rep.getValueFactory.createBNode
    val awayTeamRosterBnode = rep.getValueFactory.createBNode

    rep.addTriple(gameUri, Ontology.HAS_HOME_TEAM_ROSTER, homeTeamRosterBnode, EntityUriFactory.contextUri)
    rep.addTriple(homeTeamRosterBnode, RDF.TYPE, Ontology.ROSTER, EntityUriFactory.contextUri)
    rep.addTriple(homeTeamRosterBnode, Ontology.ROSTER_TEAM, EntityUriFactory.getTeamUri(homeTeam), EntityUriFactory.contextUri)
    rep.addTriple(homeTeamRosterBnode, RDFS.LABEL, rep.getValueFactory.createLiteral(homeTeam), EntityUriFactory.contextUri)

    rep.addTriple(gameUri, Ontology.HAS_AWAY_TEAM_ROSTER, awayTeamRosterBnode, EntityUriFactory.contextUri)
    rep.addTriple(awayTeamRosterBnode, RDF.TYPE, Ontology.ROSTER, EntityUriFactory.contextUri)
    rep.addTriple(awayTeamRosterBnode, Ontology.ROSTER_TEAM, EntityUriFactory.getTeamUri(awayTeam), EntityUriFactory.contextUri)
    rep.addTriple(awayTeamRosterBnode, RDFS.LABEL, rep.getValueFactory.createLiteral(awayTeam), EntityUriFactory.contextUri)

    val playerTeamMap: Map[String, String] = events
      .filter(_.isInstanceOf[EnterPlay])
      .map(_.asInstanceOf[EnterPlay])
      .filter(_.playerEntering.isDefined)
      .map(enterPlay => enterPlay.playerEntering.get -> enterPlay.getTeam)
      .toMap

    val teams = playerTeamMap.values.toSeq.distinct
    if (teams.size != 2)
      logger.warn(s"Found entry plays with invalid number of teams ${teams.size} for game <${gameUri}>")

    val players = playerTeamMap.keys.toSeq.distinct
    players.foreach(player => {
      rep.addTriple(EntityUriFactory.getPlayerUri(player), RDFS.LABEL, rep.getValueFactory.createLiteral(player.trim), EntityUriFactory.contextUri)
      
      val playerTeam = playerTeamMap.get(player).get
      val playerUri = EntityUriFactory.getPlayerUri(player)
      if (playerTeam == homeTeam) {
        rep.addTriple(homeTeamRosterBnode, Ontology.HAS_PLAYER, playerUri, EntityUriFactory.contextUri)
      } else if (playerTeam == awayTeam) {
        rep.addTriple(awayTeamRosterBnode, Ontology.HAS_PLAYER, playerUri, EntityUriFactory.contextUri)
      } else {
        logger.warn(s"Entry plays contain team ${playerTeam} that does match home team ${homeTeam} or away team ${awayTeam}")
      }
    })
  }

}