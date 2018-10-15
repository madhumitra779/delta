package guage
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

class GuageOps {
  private var instrumentName = ""
  private var callStrikeValue = 0.0
  private var putStrikeValue = 0.0
  private var lastPriceValue = 1.0
  def this(insName: String) {
    this()
    this.instrumentName = insName
    val UAO = tools.UtilitiesAdv(this.instrumentName)
    this.callStrikeValue = UAO.callStrike();
    this.putStrikeValue  = UAO.putStrike();
    this.lastPriceValue = tools.Utilities.lastPrice(this.instrumentName)
  }

  def sentimentCall(): Double = {
    // INCOMPLETE
    // dummy return
    return (this.callStrikeValue - this.lastPriceValue)/this.lastPriceValue
  }

  def sentimentPut(): Double = {
    // INCOMPLETE
    // dummy return
    return (this.lastPriceValue - this.putStrikeValue)/this.lastPriceValue
  }
}


object GuageOps {
  def apply(someString: String) = new GuageOps(someString)

  // initialize spark
  // TODO
  def Shalya(): Unit = {
    val coresPresent   = Runtime.getRuntime().availableProcessors()
    val coresAvailable = (coresPresent - 2).toString
    val local = "local["+coresAvailable+"]"
    val conf = new SparkConf().setMaster(local).setAppName("Delta")
    val sc = new SparkContext(conf)
    /*
     TODO
     */
    sc.stop
  }
}
