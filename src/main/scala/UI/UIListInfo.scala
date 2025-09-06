package UI

import Logic.Klist
import javafx.scene.input.{KeyCode, KeyEvent}
import scalafx.beans.property.StringProperty
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.stage.{Stage, StageStyle}

object UIListInfo:
  def apply(list: Klist, onClose: Klist => Unit) =
    val newStage = new Stage:
      title = "Logic.Card Information"
      width = 300
      height = 100
      initStyle(StageStyle.Transparent)   //Set up transparent stage for vissuals

    val root = new VBox(10):
      styleClass +="rounded-vbox"



    val titleProperty = new StringProperty(list.name)
    val titleField = new TextField():
      styleClass +="entry"
      text <==> titleProperty


    val saveButton = new Button("Save"):
      styleClass +="primary-button"
      alignment = Pos.Center
      onAction = _ =>
        onClose(Klist(list.id, titleProperty.value))
        newStage.close()

    root.children.addAll(titleField)

    root.children.addOne(saveButton)
    root.stylesheets += getClass.getResource("/styles.css").toExternalForm



    val scene = new Scene(root):
      fill=Color.Transparent
    newStage.scene = scene
    
     /*Adds keyboard shortcuts*/
    titleField.onKeyPressed = (e: KeyEvent) => 
      if (e.getCode == KeyCode.ENTER) then
        onClose(Klist(list.id, titleProperty.value))
        newStage.close()
      if (e.getCode == KeyCode.ESCAPE) then newStage.close()
    
 
    /*Closes window when not in focus*/
    newStage.focusedProperty().addListener((_, _, hasFocus) => 
      if (!hasFocus)then
        newStage.close()
    )

    newStage.show()
