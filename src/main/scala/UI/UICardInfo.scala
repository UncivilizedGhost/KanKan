package UI

import Logic.{Card, SortingFilter, TemplateCards}
import javafx.scene.input.{KeyCode, KeyEvent}

import java.time.LocalDate
import org.joda.time.DateTime
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.{HBox, Priority, Region, VBox}
import scalafx.scene.paint.Color
import scalafx.stage.{FileChooser, Stage, StageStyle}

import java.awt.Desktop
import java.io.File

object UICardInfo:
  def apply(card: Card, onClose: Card => Unit) =
    val newStage = new Stage:
      width = 400
      height = 515 + (card.todos.size * 30) // Size changes based on to-dos
      initStyle(StageStyle.Transparent) // Remove window decorations

    val root = new VBox(5):
      styleClass += "rounded-vbox"

    var noChooser = true

    /*Title field*/
    val titleLabel = new Label("Title:")
    val titleProperty = new StringProperty(card.title)
    val titleField = new TextField():
      styleClass += "entry"
      text <==> titleProperty

    /*Description field*/

    val descLabel = new Label("Description:")
    val descProperty = new StringProperty(card.description)
    val descField = new TextArea():
      styleClass += "entry"
      text <==> descProperty

    /// Gets vcard date or curent date
    val selectedDate =
      card.dueDate.map(DateTime.parse).getOrElse(DateTime.now())

    /*Date and time fields*/
    val hourField = new TextField():
      styleClass += "entry"
      promptText = "HH"
    if (card.dueDate.isDefined)
      hourField.text = selectedDate.getHourOfDay.toString

    val minuteField = new TextField():
      styleClass += "entry"
      promptText = "MM"
    if (card.dueDate.isDefined)
      minuteField.text = selectedDate.getMinuteOfHour.toString

    val datePicker = new DatePicker()

    if (card.dueDate.isDefined)
      datePicker.value = LocalDate.of(
        selectedDate.getYear,
        selectedDate.getMonthOfYear,
        selectedDate.getDayOfMonth
      )

    val timeBox = new HBox(10):
      children = Seq(hourField, minuteField, datePicker)

    /*Limits what can be entered in hour or minute sectinos*/
    hourField.text.onChange((_, oldValue, newValue) =>
      if (newValue.nonEmpty)
        try
          val hour = newValue.toInt
          if (hour < 0 || hour > 24) then hourField.text = oldValue
        catch case _: NumberFormatException => hourField.text = oldValue
    )
    minuteField.text.onChange((_, oldValue, newValue) =>
      if (newValue.nonEmpty)
        try
          val minute = newValue.toInt
          if (minute < 0 || minute > 59) then minuteField.text = oldValue
        catch case _: NumberFormatException => minuteField.text = oldValue
    )

    // Adds attachemnt
    val attachmentLabel = new Label("Atachment:")

    var name = card.attachment
    if (card.attachment == "") then name = "No File Selected"

    val link = new Hyperlink(name):
      onAction = _ =>
        val attachedFile = new File(card.attachment)
        if (attachedFile.exists) then Desktop.getDesktop.open(attachedFile)
        else new Alert(AlertType.Error, "File not found").showAndWait()
    link.disable = (card.attachment == "")

    val attachmentButton = new Button("Add Attachment"):
      styleClass += "primary-button"
      onAction = _ =>
        val fileChooser = new FileChooser
        noChooser = false
        val selectedFile = fileChooser.showOpenDialog(newStage)
        noChooser = true
        card.attachment = selectedFile.toString
        link.text = selectedFile.getName
        link.disable = (card.attachment == "")

    val deleteAttachemnt= new Button("X"):
      styleClass += "negative-button"
      onAction = _ =>
        card.attachment =""
        link.disable = (card.attachment == "")
        link.text ="No File Selected"
    /*To-do section*/

    val attachmentButtons=new HBox(2):
      children.addAll(attachmentButton,deleteAttachemnt)
    val todosLabel = new Label("Todos:")
    var todosMap = card.todos
    val todosHolder = new VBox(6)
    def updateTodos(): Unit =
      todosHolder.children.clear()

      for (todo, status) <- todosMap do
        val checkBox = new CheckBox(todo): // Adds all todoes
          styleClass += "custom-checkbox"
          selected = status
          selected.onChange((_, _, newValue) => // Changes check valued
            todosMap = todosMap + (todo -> newValue)
          )
        val removeButton = new Button("❌"):
          styleClass += "negative-button"
          styleClass += "small-button"

          onAction = _ =>
            todosMap -= todo
            updateTodos()
            newStage.height = newStage.getHeight - 30
        val spacer = new Region:
          HBox.setHgrow(this, Priority.Always) // Used to add space

        val todoBox = new HBox(checkBox, spacer, removeButton):
          maxWidth = 250

        todosHolder.children.add(todoBox)
        newStage.height =
          515 + (todosMap.size * 30) // Size is based on no. of to-dos

    updateTodos()

    val addTodoField = new TextField():
      styleClass += "entry"

    def addTodo(): Unit =
      if addTodoField.text.value.nonEmpty then
        todosMap += (addTodoField.text.value -> false)
        addTodoField.text = ""
        newStage.height = newStage.getHeight + 30

        updateTodos()

    val addTodoButton = new Button("Add Todo"):
      styleClass += "primary-button"
      onAction = _ => addTodo()

    val addTodoBox = new HBox(10, addTodoField, addTodoButton)

    /*Tags section
     *Similar to to-do*/

    val tagsLabel = new Label("Tags:")
    var tagsList = card.tags
    val tagsHolder = new HBox(10)

    def updateTags(): Unit =
      tagsHolder.children.clear()

      for tag <- tagsList do
        val singleTag = new Label(tag):
          styleClass += "tag"
          onMouseClicked = _ =>
            tagsList -= tag // Deletes tag when clicked on
            if (tagsList.isEmpty) then newStage.height = newStage.getHeight - 30
            updateTags()

        val tagBox = new HBox(7, singleTag)

        tagsHolder.children.add(tagBox)

    updateTags()

    val addTagField = new TextField():
      styleClass += "entry"

    def addTag(): Unit =
      if addTagField.text.value.nonEmpty then
        if (tagsList.isEmpty) then
          newStage.height =
            newStage.getHeight + 30 // No to-dos makes box shorter
        tagsList += (addTagField.text.value)
        addTagField.text = ""
        updateTags()

    val addTagButton = new Button("Add Tag"):
      styleClass += "primary-button"
      onAction = _ => addTag()

    val addTagBox = new HBox(10, addTagField, addTagButton)

    /*Option to selecte tempaltes*/

    val template = new ComboBox(TemplateCards.templates.keys.toSeq):
      styleClass += "combobox"
      promptText = "Choose template"
    template.value.onChange: (_, _, newValue) => // Fills all columns
      val tempCard = TemplateCards.templates(newValue)
      titleProperty.value = tempCard.title
      descProperty.value = tempCard.description
      todosMap = tempCard.todos
      updateTodos()

    val addTemplateButton = new Button("Create Template"):
      styleClass += "primary-button"
      onAction = _ =>
          TemplateCards.templates = TemplateCards.templates + (titleProperty.value -> Card(
            id = card.id,
            title = titleProperty.value,
            description = descProperty.value,
            todos = todosMap.toMap,
            tags = tagsList)
            )
          template.items= ObservableBuffer(TemplateCards.templates.keys.toSeq: _*)



    val templateBox=new HBox(2):
      children.addAll(template,addTemplateButton)


    // Returns a card with all the new values
    val saveButton = new Button("Save"):
      styleClass += "primary-button"

      onAction = _ =>
        SortingFilter.tags = SortingFilter.tags ++ tagsList
        onClose(
          Card(
            id = card.id,
            title = titleProperty.value,
            description = descProperty.value,
            tags = tagsList,
            todos = todosMap.toMap,
            attachment = card.attachment,
            dueDate =
              getSelectedDate(datePicker.value.value, hourField, minuteField)
          )
        )

        newStage.close()
      
    saveButton.disable <==titleProperty.isEmpty

    root.children.addAll(
      titleLabel,
      titleField,
      descLabel,
      descField,
      todosLabel,
      addTodoBox,
      todosHolder,
      tagsLabel,
      addTagBox,
      tagsHolder,
      attachmentLabel,
      attachmentButtons,
      link,
      timeBox,
      templateBox,
      saveButton
    )

    root.stylesheets += getClass.getResource("/styles.css").toExternalForm

    // Keyboard shortcuts added below

    root.onKeyPressed = (e: KeyEvent) =>
      if (e.getCode == KeyCode.ESCAPE) then newStage.close()

    titleField.onKeyPressed = (e: KeyEvent) =>
      if (e.getCode == KeyCode.ENTER&&(titleProperty.value!="")) then
        onClose(
          Card(
            id = card.id,
            title = titleProperty.value,
            description = descProperty.value,
            todos = todosMap.toMap,
            tags = tagsList,
            attachment = card.attachment,
            dueDate =
              getSelectedDate(datePicker.value.value, hourField, minuteField)
          )
        )
        newStage.close()
      if (e.getCode == KeyCode.ESCAPE) then newStage.close()

    addTodoField.onKeyPressed = (e: KeyEvent) =>
      if (e.getCode == KeyCode.ENTER) then addTodo()

    addTagField.onKeyPressed = (e: KeyEvent) =>
      if (e.getCode == KeyCode.ENTER) then addTag()

    newStage // Makes it only exist
      .focusedProperty()
      .addListener((_, _, hasFocus) =>
        if (!hasFocus && noChooser) then newStage.close()
      )

    val scene = new Scene(root):
      fill = Color.Transparent

    newStage.scene = scene
    newStage.show()

  /*Retusn date only if available, Otherwise retusn current date and no time*/
  def getSelectedDate(
    choosenDate: LocalDate,
    hourField: TextField,
    minuteField: TextField
  ): Option[String] =
    if (
      choosenDate != null && hourField.text.value.nonEmpty && minuteField.text.value.nonEmpty
    ) then
      val selectedDate = new DateTime(
        choosenDate.getYear,
        choosenDate.getMonthValue,
        choosenDate.getDayOfMonth,
        hourField.text.value.toInt,
        minuteField.text.value.toInt,
        0,
        0
      )
      Some(selectedDate.toString)
    else None
