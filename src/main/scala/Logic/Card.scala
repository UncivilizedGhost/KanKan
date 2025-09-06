package Logic

import com.github.nscala_time.time.StaticDateTime

case class Card(
  val id: Int,
  var title: String,
  var description: String = "",
  var tags: Set[String] = Set[String](),
  var todos: Map[String, Boolean] = Map[String, Boolean](),
  var creationDate: String =
    StaticDateTime
      .now()
      .toString, // Dates are saved as string for saving purposes
  var dueDate: Option[String] = None,
  var attachment: String = ""
):
  def reassign(c: Card) =
    title = c.title
    description = c.description
    tags = c.tags
    todos = c.todos
    creationDate = c.creationDate
    dueDate = c.dueDate
    attachment = c.attachment

  /*Returns percentadge of tasks done*/
  def tasksDone: Double =
    if (todos.isEmpty) then -1.0
    else
      var done = 0
      todos.values.foreach(x => if x then done = done + 1)
      done / ((todos.size).toDouble)

//Value for card height based on things it had
  def cardheight:Double=
    var height=100.0
    if (!description.isBlank) then
      val lines=description.count(x=>x=='\n')
      if (lines==1)
        height=120
      if (lines==2)
        height=150

    if (tags.nonEmpty) then
      height=height*1.2
    if (tasksDone!= -1) then
      height=height*1.2
    if (dueDate.isDefined) then
      height=height*1.2

    height