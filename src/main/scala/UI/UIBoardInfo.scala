package UI

import Logic.Board
import UI.UIListInfo.getClass
import javafx.scene.input.{KeyCode, KeyEvent}
import scalafx.beans.property.StringProperty
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.VBox
import scalafx.stage.{Stage, StageStyle}
import scalafx.scene.paint.Color



object UIBoardInfo:
  def apply(board: Board, onClose: Board => Unit) =
    val newStage = new Stage:
      width = 300
      height = 100
      initStyle(StageStyle.Transparent)

    val root = new VBox(10):
      styleClass +="rounded-vbox"

    val titleProperty = new StringProperty(board.name)
    val titleField = new TextField():
      text <==> titleProperty


    val saveButton = new Button("Save"):
      styleClass +="primary-button"
      alignment = Pos.Center
      onAction = _ =>
        onClose(Board(board.id, titleProperty.value,board.kLists,board.archive))
        newStage.close()

    root.children.addAll(titleField,saveButton)
    root.stylesheets += getClass.getResource("/styles.css").toExternalForm

    val scene = new Scene(root):
      fill=Color.Transparent   //Transaprent scene for rounded corners

    newStage.scene = scene

    /*Adds keyboard shortcuts*/
    titleField.onKeyPressed = (e: KeyEvent) => 
      if (e.getCode == KeyCode.ENTER) then
        onClose(Board(board.id, titleProperty.value,board.kLists,board.archive))
        newStage.close()
      if (e.getCode == KeyCode.ESCAPE) then newStage.close()
    
    /*Closes window when not in focus*/
    newStage.focusedProperty().addListener((_, _, hasFocus) => 
      if (!hasFocus)then
        newStage.close()
    )

    newStage.show()
