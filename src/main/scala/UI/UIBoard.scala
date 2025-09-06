package UI

import Logic.{Board, DragDrop, Klist}
import UI.UIBoard.getClass
import javafx.scene.input.MouseEvent
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{HBox, Priority, Region, VBox}
import scalafx.stage.Screen
import scala.util.Random

object UIBoard:
  var deferredRefresh: () => Unit = () => {}

  def apply(board: Board): HBox =

    val mainHBox = new HBox(2):
      styleClass += "board-hbox"
      styleClass += "background"

      prefWidth = Screen.primary.bounds.width // As wide as the PC window

    var kListBoxs = Seq[VBox]()
    var boxList = Map[VBox, Klist]()

    val archiveList = UIArchiveList(board.archive, board)

    val newListButton = new Button("+"):
      prefHeight = 180
      styleClass += "add-button"

    def refresh(): Unit =
      mainHBox.children.clear()
      kListBoxs = Seq[VBox]()
      for (klist <- board.kLists.reverse)
        val listNameLabel = new Label(klist.name):
          styleClass += "pos-title"
        listNameLabel.setOnDragDetected((event: MouseEvent) =>
          DragDrop.draggedKList = Some(klist)
          deferredRefresh = () => refresh()
        )

        val deleteList = new Button("X"):
          styleClass += "negative-button"
          style = "-fx-padding: 7px 15px;"
          onAction = _ =>
            board.removeList(klist)
            refresh()
        def namechange(nn: Klist): Unit =
          klist.name = nn.name
          refresh()

        listNameLabel.setOnMouseClicked((event: MouseEvent) =>
          UIListInfo(klist, namechange)
        )

        val listTop = new HBox(5)
        listTop.children.addOne(listNameLabel)
        listTop.children.addOne(deleteList)

        val tempVbox = new VBox()

        tempVbox.children.addOne(listTop)

        tempVbox.children.addOne(UiKlist(klist, board.archive))

        tempVbox.onMouseEntered = _ => // Swap lists when dragged
          if DragDrop.draggedKList.isDefined then
            val s1 =
              board.kLists.filter(x => x.name == boxList(tempVbox).name).head
            val s2 = board.kLists
              .filter(x => x.name == DragDrop.draggedKList.get.name)
              .head
            board.swap(s1, s2)

            tempVbox.children.addOne(UiKlist(klist, board.archive))

            refresh()
            deferredRefresh()

          DragDrop.draggedKList = None
        kListBoxs = kListBoxs :+ (tempVbox)
        boxList += (tempVbox -> klist)

      mainHBox.children = kListBoxs
      mainHBox.children.addOne(newListButton)

      val archiv = UIArchiveList(board.archive, board)

      archiv.onMouseMoved = _ =>
        if (DragDrop.archive) then // Resets board when card unarchived
          refresh()
          DragDrop.archive = false

      val spacer = new Region:
        HBox.setHgrow(this, Priority.Always)
      mainHBox.children.addAll(spacer, archiv)

      mainHBox.spacing = 10
      mainHBox.padding = Insets(10)

    refresh()

    newListButton.onAction = _ => createNewList()

    mainHBox.onMouseMoved = _ => // Reset board when card is archived
      if (DragDrop.archive) then
        refresh()
        DragDrop.archive = false

    /*Create empty list and open its name*/
    def createNewList(): Unit =
      val c = Klist(Random.nextInt(10000), "")

      def save(nlist: Klist): Unit =
        c.name = nlist.name
        board.addList(c)
        refresh()
      UIListInfo(c, save)

    mainHBox.stylesheets += getClass.getResource("/styles.css").toExternalForm

    mainHBox
