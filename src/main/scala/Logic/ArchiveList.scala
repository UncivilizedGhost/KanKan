package Logic

case class Archivelist(
  var originalCards: Seq[Card] = Seq(),
  var ListIds: Seq[Int] =
    Seq() // Cards and corrspending lists are stored seprarly for saving purposes
):

  def addCard(card: Card, list: Klist): Unit =
    originalCards :+= card
    ListIds :+= list.id

  def removeCard(card: Card): Unit =
    val index = originalCards.indexOf(card)
    if index != -1 then // Remove cards and list at their index
      originalCards = originalCards.patch(index, Nil, 1)
      ListIds = ListIds.patch(index, Nil, 1)

  def unarchive(card: Card, boards: Seq[Board]): Unit =
    val index = originalCards.indexOf(card)
    if index != -1 then
      val listId = ListIds(index)
      boards
        .flatMap(_.kLists)
        .find(_.id == listId)
        .foreach(list => list.addCard(card) // Adds card to original list
        )
      originalCards =originalCards.filter(x=>x.id!=card.id)
      ListIds = ListIds.patch(index, Nil, 1)
    DragDrop.archive = true // Ensure screen refresh
