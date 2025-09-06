package Logic

import scala.util.Random

object TemplateCards:

  /*Conatains templates for cards*/

  var templates: Map[String, Card] = Map(
    "Homework" -> Card(
      id = Random.nextInt(1000),
      title = "Homework",
      description = "Enter details",
      todos =
        Map("Question 1" -> false, "Question 2" -> false, "Question 3" -> false)
    ),
    "Shopping List" -> Card(
      id = Random.nextInt(1000),
      title = "Shopping List",
      description = "Weekly groceries",
      todos = Map("Milk" -> false, "Eggs" -> false, "Bread" -> false)
    ),
    "Coding Plan" -> Card(
      id = Random.nextInt(1000),
      title = "Coding Plan",
      description = "Learn language ###",
      todos = Map(
        "Get video source" -> true,
        "Learn syntax" -> false,
        "Code project" -> false
      )
    ),
    "Party Plan" -> Card(
      id = Random.nextInt(1000),
      title = "Party Plan",
      description = "Checklist for planning",
      todos = Map(
        "Invite guests" -> false,
        "Order food" -> false,
        "Book venue" -> false
      )
    ),
    "Chores" -> Card(
      id = Random.nextInt(1000),
      title = "Chores",
      description = "Cleaning plan",
      todos = Map("Laundry" -> false, "Vacuum" -> false, "Dishes" -> false)
    ),
    "Grading" -> Card(
      id = Random.nextInt(1000),
      title = "Grade KanKan",
      description = "Bribe:🍪",
      todos = Map("Give a 5" -> false, "Take a bribe" -> false)
    )
  )


  def addTemplates(newTemplates: Map[String, Card]): Unit = {
    templates = templates ++ newTemplates
  }