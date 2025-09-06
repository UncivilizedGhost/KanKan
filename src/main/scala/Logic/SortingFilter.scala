package Logic

import org.joda.time.DateTime

object SortingFilter:

  var search = ""
  var tags = Set[String]()
  var selectedTags = Set[String]()
  var sortField = "Name"
  var reverse = false

  def filter(cards: Seq[Card]): Seq[Card] =
    var finalCards = cards
    if (selectedTags.nonEmpty)
      for tag <- selectedTags do
        finalCards = finalCards.filter(x => x.tags.contains(tag))
    if (search.nonEmpty)
      finalCards = finalCards.filter(x =>
        x.title.toLowerCase.contains(
          search.toLowerCase
        ) || x.description.toLowerCase.contains(search.toLowerCase)
      ) // Non-Case-sensitive search

    finalCards

  def sort(cards: Seq[Card]): Seq[Card] =
    var sortedCards = cards
    if (sortField == "Name") then sortedCards = cards.sortBy(x => x.title)
    else if (sortField == "Desription")
      sortedCards = cards.sortBy(x => x.description)
    else if (sortField == "Creation Date")
      sortedCards = cards.sortBy(x => DateTime(x.creationDate))
    else if (sortField == "Due Date")
      sortedCards = cards.sortBy(x =>
        if (x.dueDate.isDefined) then DateTime(x.dueDate.get)
        else
          DateTime(1900, 1, 1, 0, 0, 0,
            0) // Cards with no due date are shown first
      )
    else
      sortedCards = cards.sortBy(x => x.id)
    if (reverse)
      sortedCards = sortedCards.reverse
    sortedCards
