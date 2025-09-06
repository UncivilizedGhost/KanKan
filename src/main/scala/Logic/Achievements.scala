package Logic

import UI.UIAchievement

object Achievements:
  var archiveLevel=0
  var addLevel=0
  var dragLabel=0
   //Actual valus would be higher for long term use
  def arcExp()=
    archiveLevel+=1
    archiveLevel match
      case 5=>UIAchievement("Finisher")
      case 15=>UIAchievement("Completionist")
      case 30=>UIAchievement("Tax Fraud commiter")
      case _=>()
  def addExp()=
    addLevel+=1
    addLevel match
      case 5=>UIAchievement("1+1+1")
      case 15=>UIAchievement("Additional testing")
      case 30=>UIAchievement("ADDiction")
      case _=>()

  def wallExp()=
    dragLabel+=1
    dragLabel match
      case 5=>UIAchievement("Yes it works")
      case 15=>UIAchievement("Placeholder Joke")
      case 30=>UIAchievement("Funny joke that is cut off because i made it too long on purpose. I know it isn't actually funny but I'm too tired to come up with somethin better.")
      case _=>()

  def saveLevels()=//Returns values for saving
      List (
        addLevel,
        dragLabel,
        archiveLevel
      )