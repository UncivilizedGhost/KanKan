package UI

import Logic.{Achievements, Archivelist, Card, DragDrop, Klist}
import scalafx.scene.layout.{HBox, VBox}
import javafx.scene.input.MouseEvent
import scalafx.scene.control.Button

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import scala.util.Random


object UiKlist:
  var deferredRefresh: () => Unit = () => {}
  def apply(kList: Klist, archive: Archivelist): VBox =
    
    val mainVBox = new VBox:   //Main box for stroing everying
      styleClass += "list-hbox"
      spacing = 12
      prefHeight  = 1080
      prefWidth  = 190

    //Cards are created as a box with buttons made sepratly
    var cardBoxes = Map[VBox, HBox]()   //Map connecting every cardbox to it's buttons
    var boxList = Map[VBox, Card]() // Map connecting every card to it's box



    val newCardButton = new Button("+"):
      styleClass+="big-button"
      styleClass+="add-button"
      prefWidth = 185


    def newCardButtonBox(c: Card): HBox =
      val buttonBox = new HBox(4)
      val copyButton = new Button("Copy"):
        styleClass+="primary-button"
        onAction = _ =>  //Copy's title
          val selection = new StringSelection(c.description)
          val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
          clipboard.setContents(selection, null)

      val deleteButton = new Button("Delete"):  //Removes card completly
        styleClass+="negative-button"
        onAction = _ =>
          kList.removeCard(c)
          refresh()

      val archiveButton = new Button("Archive"):  //Sends to archive list and removes from original list
        styleClass+="primary-button"  
        onAction = _ =>
          kList.removeCard(c)
          DragDrop.archive=true
          archive.addCard(c, kList)
          Achievements.arcExp()

          refresh()

      buttonBox.children.addAll(copyButton, deleteButton, archiveButton)
      buttonBox

    def refresh(): Unit =
      cardBoxes = Map[VBox, HBox]()
      boxList = Map[VBox, Card]()
      mainVBox.children.clear()

      kList.sortList()

      /*Filtered cards are used to create a card box which is the card and it's buttons are mapped to*/
      for card <- kList.filterList() do
        val box = UICard(card)
        cardBoxes += (box -> newCardButtonBox(card))
        boxList += (box -> card)

      cardBoxes.foreach:
        case (cardBox, buttonBox) =>
        val singleCardBox = new VBox(2)
        singleCardBox.children.addAll(cardBox, buttonBox)

         /*When drag is detected card is saved in drag drop object
         * The list is also saved
         * A deffered refresh funciton is sent so when the drag is over the list will be refreseh
         * This allows it to update list when drag is over*/
        singleCardBox.setOnDragDetected((event: MouseEvent) =>  
          DragDrop.draggedCard = Some(boxList(cardBox))
          DragDrop.draggedList = Some(kList)
          deferredRefresh = () => refresh()
        )

        mainVBox.children.addOne(singleCardBox)
      

      mainVBox.children.addOne(newCardButton)


    refresh()

    /*Finiseh the card drag*/
    mainVBox.onMouseEntered = _ =>
      // Checks card is being dragged
      if DragDrop.draggedCard.isDefined && DragDrop.draggedList.exists(_ != kList) then
        kList.addCard(DragDrop.draggedCard.get)
        DragDrop.draggedList.get.removeCard(DragDrop.draggedCard.get) //Removes card from original list
        //Refreshes both liss
        refresh()
        deferredRefresh()
        //Resets drag object and hides drag rectangle once drag is open
        DragDrop.reset()
        dragRectangle.visible=false
  
    newCardButton.onAction = _ =>
      createNewCard()

    def createNewCard(): Unit =
      val c = Card(Random.nextInt(1000), "")//Creates empty card with ranodm id


      def save(ncard: Card): Unit =  //Saves card to list
        c.reassign(ncard)
        kList.addCard(c)
        refresh()
        Achievements.addExp() 



      UICardInfo(c, save)  //Opens new card

    mainVBox.setMaxHeight(Double.MaxValue)  //Makes list as long as possible
    mainVBox
