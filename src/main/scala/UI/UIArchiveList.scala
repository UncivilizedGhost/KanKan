package UI

import Logic.{Archivelist, Board, SortingFilter}
import scalafx.scene.control.{Button, Label, ScrollPane}
import scalafx.scene.layout.VBox
import scalafx.stage.Screen

object UIArchiveList:
  def apply(archive: Archivelist, board: Board): VBox =
    val screenHeight = Screen.primary.visualBounds.height

    val archiveBox = new VBox:
      styleClass += "archive-list"

      prefWidth = 185
      prefHeight = screenHeight-80

    def refresh(): Unit =
      archiveBox.children.clear()
      val arcLabel = new Label("🗃 Archive"):
        styleClass += "neg-title"
      archiveBox.children.addOne(arcLabel)

      for (card <- archive.originalCards) do
        val cardBox = new VBox(8):
          styleClass += "card-box"
          prefWidth = 185
          children += new Label(card.title):
            styleClass += "title"

        val unarchiveButton = new Button("Unarchive"):
          prefWidth = 180
          styleClass += "primary-button"
          onAction = _ =>
            archive.unarchive(card, Seq(board))
            SortingFilter.tags = SortingFilter.tags ++ card.tags
            refresh()

        val fullBox = new VBox(3)
        fullBox.children.addAll(cardBox, unarchiveButton)
        archiveBox.children.addOne(fullBox)

    refresh()

    val scrollPane = new ScrollPane: //Store in a scrollpane for scrolling
      content = archiveBox
      fitToWidth = true
      hbarPolicy = ScrollPane.ScrollBarPolicy.Never
      vbarPolicy = ScrollPane.ScrollBarPolicy.Never
      styleClass += "scroll-pane"

    val finalBox=new VBox:
      children = scrollPane
      prefWidth = 185
    finalBox