package com.stellmangreene.pbprdf.plays.test

import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import com.stellmangreene.pbprdf.GamePeriodInfo
import com.stellmangreene.pbprdf.plays.TimeoutPlay
import com.stellmangreene.pbprdf.test.TestIri

import com.stellmangreene.pbprdf.util.RdfOperations._

/**
 * Test the TimeoutPlay class
 *
 * @author andrewstellman
 */
class TimeoutPlaySpec extends FlatSpec with Matchers {

  behavior of "TimeoutPlay"

  // As long as each event has unique game and event IDs, they can all go into the same repository
  val rep = new SailRepository(new MemoryStore)
  rep.initialize

  it should "parse an official timeout" in {
    val testIri = TestIri.create("400610636")
    val play = new TimeoutPlay(testIri, 125, 2, "4:56", "Mystics", "Official timeout", "10-9", GamePeriodInfo.WNBAPeriodInfo)
    play.addRdf(rep)

    val statements = rep
      .executeQuery("SELECT * { <http://stellman-greene.com/pbprdf/400610636/125> ?p ?o }")
      .map(statement => (s"${statement.getValue("p").stringValue} -> ${statement.getValue("o").stringValue}"))
      .toSet

    statements should be(
      Set(
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Play",
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Timeout",
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Event",
        s"http://stellman-greene.com/pbprdf#inGame -> ${testIri.stringValue}",
        "http://stellman-greene.com/pbprdf#period -> 2",
        "http://stellman-greene.com/pbprdf#time -> 4:56",
        "http://stellman-greene.com/pbprdf#secondsIntoGame -> 904",
        "http://stellman-greene.com/pbprdf#secondsLeftInPeriod -> 296",
        "http://stellman-greene.com/pbprdf#forTeam -> http://stellman-greene.com/pbprdf/teams/Mystics",
        "http://stellman-greene.com/pbprdf#isOfficial -> true",
        "http://www.w3.org/2000/01/rdf-schema#label -> Mystics: Official timeout"))
  }

  it should "parse a full timeout" in {
    val testIri = TestIri.create("400610636")
    val play = new TimeoutPlay(testIri, 327, 2, "7:05", "Sun", "Connecticut Full timeout", "25-26", GamePeriodInfo.WNBAPeriodInfo)
    play.addRdf(rep)

    val statements = rep
      .executeQuery("SELECT * { <http://stellman-greene.com/pbprdf/400610636/327> ?p ?o }")
      .map(statement => (s"${statement.getValue("p").stringValue} -> ${statement.getValue("o").stringValue}"))
      .toSet

    statements should be(
      Set(
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Play",
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Timeout",
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Event",
        s"http://stellman-greene.com/pbprdf#inGame -> ${testIri.stringValue}",
        "http://stellman-greene.com/pbprdf#period -> 2",
        "http://stellman-greene.com/pbprdf#time -> 7:05",
        "http://stellman-greene.com/pbprdf#secondsIntoGame -> 775",
        "http://stellman-greene.com/pbprdf#secondsLeftInPeriod -> 425",
        "http://stellman-greene.com/pbprdf#forTeam -> http://stellman-greene.com/pbprdf/teams/Sun",
        "http://stellman-greene.com/pbprdf#timeoutDuration -> Full",
        "http://www.w3.org/2000/01/rdf-schema#label -> Sun: Connecticut Full timeout"))
  }
}
