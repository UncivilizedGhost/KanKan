package UI


import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

object DragRectangle:
  /*Rectangle used when dragign objects*/
  def apply(): Rectangle = new Rectangle:
    width = 185    
    height = 100
    fill = Color.web("#2D539E", 0.3)
    stroke = Color.web("#2D539E")
    strokeWidth = 2
    arcWidth = 15
    arcHeight = 15
    mouseTransparent = true
    visible = false

val dragRectangle = DragRectangle()
