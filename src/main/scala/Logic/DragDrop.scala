package Logic

object DragDrop:
  // Stores list of card being dragged
  var draggedCard: Option[Card] = None
  var draggedList: Option[Klist] = None

  // Stores list being dragged
  var draggedKList: Option[Klist] = None

  // stores wherether a card has been archived
  var archive = false

  def reset() =
    draggedCard = None
    draggedList = None
    draggedKList = None
