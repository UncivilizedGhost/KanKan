package Logic

import io.circe.Printer

import java.nio.file.{Files, Paths}
import scala.collection.mutable
import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*

object SaveLoad:

  def loadFiles(): mutable.Set[Board] =    
    //Returns a set of boards and sets the acheivements and templates automatically
    var allboards: mutable.Set[Board] = mutable.Set()
    val filePath = "Save Files/save.json"
    val path = Paths.get(filePath)
    if (Files.exists(path)) then
      val jsonStr = new String(Files.readAllBytes(path))
      val result = decode[mutable.Set[Board]](jsonStr)

      result match
        case Right(boards) =>
          allboards = boards
        case Left(error) => // If save file is not laodable new board is created
          val mboard = Board(id = 1, name = "Main")
          allboards = mutable.Set(mboard)
    else
      val mboard = Board(id = 1, name = "Main")
      allboards = mutable.Set(mboard)

    if (Files.exists(Paths.get("Save Files/exp.json"))) then
      val jsonStr = new String(Files.readAllBytes(Paths.get("Save Files/exp.json")))
      val result = decode[List[Int]](jsonStr)
      result match
        case Right(value) =>

          Logic.Achievements.addLevel=value.head
          Logic.Achievements.dragLabel=value(1)
          Logic.Achievements.archiveLevel=value(2)
        case Left(_) => ()

    if (Files.exists(Paths.get("Save Files/templates.json"))) then
      val jsonStr = new String(Files.readAllBytes(Paths.get("Save Files/templates.json")))
      val result = decode[Map[String, Card]](jsonStr)
      result match
        case Right(value) =>
          TemplateCards.addTemplates(value)
        case Left(_) => ()

    allboards


  def saveFiles(allboards:mutable.Set[Board])=
      Files.write(
        Paths.get("Save Files/save.json"), // Save file when app closes
        Printer.spaces2.print(allboards.asJson).getBytes
      )
      val expSet = Logic.Achievements.saveLevels()
      Files.write(
        Paths.get("Save Files/exp.json"),
        Printer.spaces2.print(expSet.asJson).getBytes
      )
      Files.write(
        Paths.get("Save Files/templates.json"), // Save file when app closes
        Printer.spaces2.print(Logic.TemplateCards.templates.asJson).getBytes
      )
