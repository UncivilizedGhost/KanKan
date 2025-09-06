package UI

import UI.UICardInfo.getClass
import scalafx.animation.AnimationTimer
import scalafx.scene.Scene
import scalafx.scene.control.{Label, ProgressBar}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.stage.{Stage, StageStyle}
import scalafx.animation.{AnimationTimer, FadeTransition, TranslateTransition}
import scalafx.util.Duration

object UIAchievement:
  /*Small message when a new achivement is achieved*/
  def apply(message: String): Unit =

    val achivemenentStage = new Stage:
      initStyle(StageStyle.Transparent)
      alwaysOnTop = true

    val label = new Label(message):
      styleClass+="title"

    val progressBar = new ProgressBar:
      prefWidth = 300
      progress = 1.0



    val box = new VBox:
      spacing = 5
      children = Seq(label, progressBar)
      styleClass+="ach-vbox"

    box.stylesheets += getClass.getResource("/styles.css").toExternalForm

    achivemenentStage.scene = new Scene:
      fill = Color.Transparent
      root = box


    achivemenentStage.x = 900
    achivemenentStage.y = 30

    achivemenentStage.show()
    val slideAnimation = new TranslateTransition(Duration(500), box):
      fromX = 400
      toX = 0
      cycleCount = 1

    slideAnimation.play()


    val startTime = System.nanoTime()

    //windows closes autommatically as the loading bar goes down
    lazy val animationTimer: AnimationTimer = AnimationTimer: now =>
      val passedTime = (now - startTime) / 1e9
      val remainingTime = 2 - passedTime

      if remainingTime <= 0 then
        achivemenentStage.close()
        animationTimer.stop()
      else
        progressBar.progress = remainingTime / 2.0

    animationTimer.start()
