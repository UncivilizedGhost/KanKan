import Logic.{Achievements, Board, DragDrop, SaveLoad}
import UI.{Menu, dragRectangle}
import scalafx.application.JFXApp3
import javafx.scene.Cursor
import scalafx.scene.Scene
import scalafx.scene.layout.VBox
import javafx.scene.text.Font
import scalafx.Includes.jfxMouseEvent2sfx

import scala.util.Try

object Main extends JFXApp3:

  /*Load content from json file*/
  var allboards=Logic.SaveLoad.loadFiles()

  for b <- allboards do b.addtags() // Add tags to gloabl tag list

  def start() =
    // Try to load fonts
    Try:
      Font.loadFont(
        getClass.getResourceAsStream("Font/RobotoMono-Bold.ttf"),
        12
      )
      Font.loadFont(
        getClass.getResourceAsStream("Font/Inter_24pt-Regular.ttf"),
        12
      )

    stage = new JFXApp3.PrimaryStage:
      title = "KanKan"
      maximized = true // Set window to max size by efault

    val root = new scalafx.scene.layout.Pane() // Use pane for drag animation
    val scene = Scene(parent = root)
    stage.scene = scene

    // If card is dragged somewhere it shouldn't be it the dragged card is removed
    scene.onMouseMoved = event =>
      if DragDrop.draggedCard.isDefined then
        DragDrop.reset()
        Achievements.wallExp()
      dragRectangle.visible = false
      for b <- allboards do b.addtags()
      scene.setCursor(Cursor.DEFAULT)

    scene.onMouseDragged =
      event => // If a card begans to drag a rectangle appears to show it
        if DragDrop.draggedCard.isDefined then
          dragRectangle.visible = true
          dragRectangle.layoutX = event.x
          dragRectangle.layoutY = event.y
          scene.setCursor(Cursor.NONE) // Hide the cursor when dragged

    stage.setOnCloseRequest(_ => SaveLoad.saveFiles(allboards)) //Save everythign

    val app = Menu.makeMenu(allboards) // Start actual app
    root.children.addAll(app, dragRectangle)

  end start

end Main
