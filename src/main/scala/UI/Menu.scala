package UI

//Imports
import Logic.{Board, SortingFilter}
import UI.Menu.getClass
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.{HBox, Priority, Region, VBox}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

import java.nio.file.{Files, Paths, StandardCopyOption}
import scala.collection.mutable
import scala.util.Random

object Menu:

  def makeMenu(boards: mutable.Set[Board]): VBox =

    val appbox = new VBox

    val menuBar = new HBox:
      styleClass += "menu-hbox"
      spacing = 12

    /*The first part involves creating a combox for boards and options to change them*/
    var currentBoard =
      if (boards.nonEmpty) boards.head else Board(Random.nextInt(1000), "Main")

    var options = ObservableBuffer(boards.map(_.name).toSeq: _*)
    val menuSelection = new ComboBox(options):

      promptText = options.head

    // Changes which board to show
    def updateBoard(board: Board): Unit =
      currentBoard = board
      appbox.children.clear()
      appbox.children.addAll(menuBar, UIBoard(currentBoard) )

    menuSelection.value.onChange: (_, _, newValue) =>
      boards.find(_.name == newValue).foreach(updateBoard)

    // Ootion to delete board if they are multiple boards
    val deleteBoardButton = new Button("Remove"):
      styleClass += "button"
      styleClass += "negative-button"

      disable = boards.size <= 1
      onAction = _ =>
        boards.remove(currentBoard)  //Select next board and update options
        options = ObservableBuffer(boards.map(_.name).toSeq: _*)
        menuSelection.items = options
        currentBoard = boards.head
        menuSelection.value = currentBoard.name
        updateBoard(currentBoard)
        disable = boards.size <= 1

    val renameBoardButton = new Button("I"):
      styleClass += "secondary-button"

      onAction = _ =>

        def save(
          updatedBoard: Board
        ): Unit = // Get rid of old board and add new one with save values
            val oldBoard=currentBoard
            boards.remove(oldBoard)
            boards.add(updatedBoard)
            options = ObservableBuffer(boards.map(_.name).toSeq: _*)
            menuSelection.items = options
            menuSelection.value = updatedBoard.name
            currentBoard = updatedBoard

        UIBoardInfo(currentBoard, save)

    // Add new board
    val addBoardButton = new Button("Add"):
      styleClass += "primary-button"
      onAction = _ =>
        val newBoard = Board(Random.nextInt(10000), "")

        def save(board: Board): Unit =
          boards.addOne(board)
          // Update options
          options = ObservableBuffer(boards.map(_.name).toSeq: _*)
          menuSelection.items = options
          menuSelection.value = board.name
          updateBoard(board)
        deleteBoardButton.disable = false
        UIBoardInfo(newBoard, save)

    val sorts = Seq("Name", "Description", "Creation Date", "Due Date")
    val sortSelection = new ComboBox(sorts):

      promptText = sorts.head

    SortingFilter.sortField = sorts.head

    sortSelection.value.onChange: (_, _, chosenSort) =>
      SortingFilter.sortField = chosenSort
      updateBoard(currentBoard)

    val label = new Label()
    var selectedTags = Set[String]()
    val prettyview = StringProperty("No Tags Selected")
    label.text <== prettyview

    val tagBar = new TextField():
      styleClass += "search"

    val addTagButton = new Button("Add tags"):
      styleClass += "primary-button"
      onAction = _ =>
        selectedTags = selectedTags + tagBar.text.value
        prettyview() = selectedTags.mkString(", ")
        tagBar.text.value = ""
        disable = true
    addTagButton.disable = true

    val searchBar = new TextField():
      styleClass += "search"

    val searchButton = new Button("Search"):
      styleClass += "primary-button"
      onAction = _ =>
        SortingFilter.search = searchBar.text.value
        SortingFilter.selectedTags = selectedTags
        updateBoard(currentBoard)

    val clearButton = new Button("Clear"):
      styleClass += "secondary-button"
      onAction = _ =>
        searchBar.text = ""
        SortingFilter.search = ""
        selectedTags = Set()
        SortingFilter.selectedTags = selectedTags
        prettyview() = "No Tags Selected"
        updateBoard(currentBoard)

    val wallpaperButton = new Button("🖼"):
      styleClass += "secondary-button"
      onAction = _ =>
        val chooser = new FileChooser:
          title = "Select a PNG Image"
          extensionFilters.add(ExtensionFilter("PNG Images", "*.png"))

        val window = this.getScene.getWindow
        val selectedFile = chooser.showOpenDialog(window)
        val targetPath = Paths.get("src/main/resources/wallpaper.png")
        Files.copy(
          selectedFile.toPath,
          targetPath,
          StandardCopyOption.REPLACE_EXISTING
        )
        appbox.children.remove(1)

        // Create new board and set style directly
        val boardNode = UIBoard(currentBoard)
        val uri = targetPath.toUri.toString.replace(" ", "%20")
        boardNode.setStyle(
          s"-fx-background-image: url('$uri'); -fx-background-size: cover;"
        )

        appbox.children.addOne(boardNode)

    val removeWallpaperButton = new Button("❌"):
      styleClass += "negative-button"
      onAction = _ =>
        val targetPath = Paths.get("src/main/resources/wallpaper.png")
        if (Files.exists(targetPath)) then Files.delete(targetPath)
        appbox.children.remove(1)
        val boardNode = UIBoard(currentBoard)
        boardNode.setStyle("")
        appbox.children.addOne(boardNode)

    tagBar.onKeyReleased = _ =>
      addTagButton.disable = !SortingFilter.tags.contains(tagBar.text.value)

    // Add elements to main hboxs
    menuBar.children.addAll(
      deleteBoardButton,
      addBoardButton,
      menuSelection,
      renameBoardButton
    )
    val spacer = new Region:
      HBox.setHgrow(this, Priority.Always)

    menuBar.children.addAll(
      tagBar,
      addTagButton,
      label,
      searchBar,
      searchButton,
      clearButton,
      sortSelection,
      spacer,
      wallpaperButton,
      removeWallpaperButton
    )

    appbox.children.addAll(menuBar, UIBoard(currentBoard))

    appbox.stylesheets += getClass.getResource("/styles.css").toExternalForm
    appbox
