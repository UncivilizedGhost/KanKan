package Logic


case class Klist(
  val id: Int,
  var name: String,
  var cards: Seq[Card] = Seq[Card]()
):

  def addCard(card: Card): Unit =
    cards = cards.+:(card)

  def sortList(): Unit =
    cards = SortingFilter.sort(cards)

  def filterList(): Seq[Card] =
    SortingFilter.filter(cards)

  def removeCard(card: Card): Unit =    
    if cards.contains(card) then
      SortingFilter.tags = SortingFilter.tags -- card.tags //Removes card's tags 
      cards = cards.filter(x => x != card)

  def addtags(): Unit =  //Adds tags at startup
    for c <- cards do SortingFilter.tags = SortingFilter.tags ++ c.tags
