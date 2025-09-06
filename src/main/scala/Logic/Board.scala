package Logic

case class Board(
  val id: Int,
  var name: String,
  var kLists: Seq[Klist] = Seq[Klist](),
  val archive: Archivelist = Archivelist()
):

  def addList(list: Klist): Unit =
    kLists = kLists.+:(list)

  def removeList(list: Klist): Unit =
    kLists = kLists.filterNot(x => x.id == list.id)


  def swap(element1: Klist, element2: Klist): Unit =
    val id1 = kLists.indexOf(element1)
    val id2 = kLists.indexOf(element2)
    kLists = kLists.updated(id1, element2).updated(id2, element1)

  def removeCard(card: Card): Unit =
    kLists.foreach(x => x.removeCard(card))
    
  /*Adds tags from every list to filer*/
  def addtags(): Unit =  
    for k <- kLists do k.addtags()
