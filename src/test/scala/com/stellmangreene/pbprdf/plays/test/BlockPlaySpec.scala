package com.stellmangreene.pbprdf.plays.test

import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import com.stellmangreene.pbprdf.GamePeriodInfo
import com.stellmangreene.pbprdf.plays.BlockPlay
import com.stellmangreene.pbprdf.test.TestIri

import com.stellmangreene.pbprdf.util.RdfOperations._

/**
 * Test the BlockPlay class
 *
 * @author andrewstellman
 */
class BlockPlaySpec extends FlatSpec with Matchers {

  behavior of "BlockPlay"

  // As long as each event has unique game and event IDs, they can all go into the same repository
  val rep = new SailRepository(new MemoryStore)
  rep.initialize

  it should "parse block triples" in {
    var testIri = TestIri.create("400610636")
    new BlockPlay(testIri, 108, 2, "7:48", "Mystics", "Tayler Hill blocks Jasmine Thomas's layup", "23-26", GamePeriodInfo.WNBAPeriodInfo).addRdf(rep)

    rep.executeQuery("SELECT * { <http://stellman-greene.com/pbprdf/400610636/108> ?p ?o }")
      .map(statement => (s"${statement.getValue("p").stringValue} -> ${statement.getValue("o").stringValue}"))
      .toSet should be(
        Set(
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Event",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Shot",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Play",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Block",
          s"http://stellman-greene.com/pbprdf#inGame -> ${testIri.stringValue}",
          "http://stellman-greene.com/pbprdf#period -> 2",
          "http://stellman-greene.com/pbprdf#time -> 7:48",
          "http://stellman-greene.com/pbprdf#secondsIntoGame -> 732",
          "http://stellman-greene.com/pbprdf#secondsLeftInPeriod -> 468",
          "http://stellman-greene.com/pbprdf#forTeam -> http://stellman-greene.com/pbprdf/teams/Mystics",
          "http://stellman-greene.com/pbprdf#shotBy -> http://stellman-greene.com/pbprdf/players/Jasmine_Thomas",
          "http://stellman-greene.com/pbprdf#shotBlockedBy -> http://stellman-greene.com/pbprdf/players/Tayler_Hill",
          "http://www.w3.org/2000/01/rdf-schema#label -> Mystics: Tayler Hill blocks Jasmine Thomas's layup"))

    var testIri2 = TestIri.create("400610636")
    new BlockPlay(testIri, 53, 1, "2:37", "Mercury", "Krystal Thomas blocks Erin Phillips' 3-foot  layup", "19-23", GamePeriodInfo.WNBAPeriodInfo).addRdf(rep)

    rep.executeQuery("SELECT * { <http://stellman-greene.com/pbprdf/400610636/53> ?p ?o }")
      .map(statement => (s"${statement.getValue("p").stringValue} -> ${statement.getValue("o").stringValue}"))
      .toSet should be(
        Set(
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Event",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Shot",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Play",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://stellman-greene.com/pbprdf#Block",
          s"http://stellman-greene.com/pbprdf#inGame -> ${testIri2.stringValue}",
          "http://stellman-greene.com/pbprdf#period -> 1",
          "http://stellman-greene.com/pbprdf#time -> 2:37",
          "http://stellman-greene.com/pbprdf#secondsIntoGame -> 443",
          "http://stellman-greene.com/pbprdf#secondsLeftInPeriod -> 157",
          "http://stellman-greene.com/pbprdf#forTeam -> http://stellman-greene.com/pbprdf/teams/Mercury",
          "http://stellman-greene.com/pbprdf#shotBy -> http://stellman-greene.com/pbprdf/players/Erin_Phillips",
          "http://stellman-greene.com/pbprdf#shotBlockedBy -> http://stellman-greene.com/pbprdf/players/Krystal_Thomas",
          "http://www.w3.org/2000/01/rdf-schema#label -> Mercury: Krystal Thomas blocks Erin Phillips' 3-foot  layup"))
  }

}
