package UI

import Logic.Card

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import scalafx.scene.control.{Label, ProgressBar}
import scalafx.scene.layout.{HBox, Priority, Region, VBox}

object UICard:

  def apply(card: Card): VBox =
    val cardBox = new VBox(2):
      styleClass += "card-box"
      prefWidth = 185

    def refresh() =
      val title = new Label(card.title):
        styleClass += "title"
      val description = new Label(card.description):
        styleClass += "description"
      description.setWrapText(true)

      description.setMaxHeight(60)  //Descirption can only have 3 lines shown

      val dueTime = new Label( // Only has content if date and time exist
        card.dueDate match
          case Some(due) =>
            s"        📅${due.substring(0, 10)}\n              ⏰${due.substring(12, 16)}"
          case None => ""
      ):
        styleClass += "description"
      val spacer = new Region:
        HBox.setHgrow(this, Priority.Always)
      val tagbox = new HBox()
      tagbox.children.addOne(spacer)

      val introTags = card.tags.take(3)

      for (tag <- introTags) do
        val tagLabel = new Label(tag):
          styleClass += "tag-small"
        tagbox.children.addOne(tagLabel)

      cardBox.children.addAll(title, description, tagbox, dueTime)

      if (card.tasksDone != -1)
        val taskdone = new ProgressBar():
          prefWidth = 220
        taskdone.setProgress(card.tasksDone)
        cardBox.children += taskdone
      cardBox.prefHeight = card.cardheight //Sets hiehgt based on number of elements

    refresh()

    def save(ncard: Card): Unit =
      card.reassign(ncard)
      cardBox.children.clear()
      refresh()

    cardBox.setOnMouseClicked((event: MouseEvent) =>
      UICardInfo(card, save)
    )

    cardBox.stylesheets += getClass.getResource("/styles.css").toExternalForm

    cardBox
