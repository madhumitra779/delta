package guage
import scala.collection.mutable.{ArrayBuffer, StringBuilder}
import scala.util.matching.Regex
import java.text.SimpleDateFormat

object Adwitiya {
  // time Left till execution
  // this function holds good if and only if the expiration fall in the current month
  def timeLeft(instumentName: String, identifier: String): Int = {
    val obj = tools.UtilitiesAdv(instumentName)
    val dateNow = new SimpleDateFormat("yyyy-MM-dd")
      .format(new java.util.Date).toString.split('-').toArray
    val days = dateNow(2).toInt

    val optionDate = new SimpleDateFormat("yyyy-MM-dd")
      .format(obj.forDate(identifier))
      .toString.split('-').toArray
    val optionDays = optionDate(2).toInt
    if((optionDays < days) && (optionDate(1).toInt > dateNow(1).toInt)) {
      // do some magic
      val d = Array(1,3,5,7,8,10,12)
      if(d.intersect(Array(dateNow(1).toInt)).length != 0) {
        return (31 - days) + optionDays
      }
      else {
        return (30 - days) + optionDays
      }
    }
    else {
      return optionDays - days
    }
  }

  // construction of condor
  // construction of butterfly
}
