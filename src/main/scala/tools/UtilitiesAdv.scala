package tools
import scala.util.matching.Regex
import scala.collection.mutable.ArrayBuffer

class UtilitiesAdv {
  private var instrumentName  = ""
  private var callStringStrip = ""
  private var putStringStrinp = ""
  private var identifyingName = ""

  def yahooURL(instrumentName: String): String = {
    return "https://query1.finance.yahoo.com/v7/finance/options/"+instrumentName
  }

  def findString(patternString: String, inThisString: String): String = {
    val patternRegex = patternString.r
    val findings = patternRegex.findAllIn(inThisString).toArray
    if(findings.length == 0)
      return "0.0"
    else
      return findings(0).toString
  }

  def findStringArray(patternString: String, inThisString: String): Array[String] = {
    val patternRegex = patternString.r
    val findings = patternRegex.findAllIn(inThisString).toArray
    return findings
  }

  def maxSearchString(wCall: String): String = {
    val searchStringArray = findStringArray("\"volume\":([0-9. ]+)", wCall)
    val searchArrayDouble = scala.collection.mutable.ArrayBuffer[Double]()
    for(i <- searchStringArray) {
      searchArrayDouble += (i.replaceAllLiterally("\"volume\":", "").toDouble)
    }
    val maximum = Utilities.maxElement(searchArrayDouble.toArray)
    return "\"volume\":"+maximum.toInt.toString
  }

  def urlStringEngine(instrumentName: String): String = {
    var returningString = ""
    try {
      returningString = scala.io.Source
        .fromURL(yahooURL(instrumentName)).mkString
    } catch {
      case fileIO: java.io.FileNotFoundException => returningString = "0.0";
    }

    if(returningString != "0.0")
    {
      // put a limit to this madness
      var maxCount: Int = 0;
      do {
        maxCount = maxCount + 1;
        returningString = scala.io.Source.fromURL(yahooURL(instrumentName))
          .mkString
      }
      while(((findString("\"result\":(....)", returningString)
        .replaceAllLiterally("\"result\":", "") == "null")) && (maxCount != 10))
    }
    return returningString;
  }

  def getURL(instrumentName: String): String = {
    var urlString = "";
    var maxCount: Int = 0;
    do {
      urlString = urlStringEngine(instrumentName)
      maxCount = maxCount + 1;
    } while (urlString == "0.0" && maxCount != 10)
      
    return urlString;
  }

  // a typical scala 2.11.8 constructor
  def this(insName: String) {
    this()
    this.instrumentName = insName
    val inThisString = getURL(instrumentName)
    val callAndPut   = inThisString.split("\\}\\],\"puts\":\\[\\{")
    val callString   = callAndPut(0)
    val putString    = callAndPut(1)

    val callStrip = findString("\\{([a-zA-Z0-9.,:\"\\- ]+)"
      +maxSearchString(callString)
      +"([a-zA-Z0-9.,:\"\\- ]+)\\},", callString)
    val putStrinp = findString("\\{([a-zA-Z0-9.,:\"\\- ]+)"
      +maxSearchString( putString)
      +"([a-zA-Z0-9.,:\"\\- ]+)\\},",  putString)
    this.callStringStrip = callStrip
    this.putStringStrinp = putStrinp
    this.identifyingName = findString("\"longName\":\"([a-zA-Z0-9 ]+)",
      inThisString).replaceAllLiterally("\"longName\":\"", "").toString      
  }

  def generateValue(fieldName: String, callOrPut: String): Double = {
    if (callOrPut == "call")
      return findString("\""+fieldName+"\":([0-9. ]+)", callStringStrip)
        .replaceAllLiterally("\""+fieldName+"\":", "")
        .toDouble
    else
      return findString("\""+fieldName+"\":([0-9. ]+)", putStringStrinp)
        .replaceAllLiterally("\""+fieldName+"\":", "")
        .toDouble
  }

  def call(): Double = {
    return generateValue("lastPrice", "call")
  }

  def callVolume(): Long = {
    return generateValue("volume", "call").toLong
  }

  def callStrike(): Double = {
    return generateValue("strike", "call")
  }

  def put(): Double = {
    return generateValue("lastPrice", "put")
  }

  def putStrike(): Double = {
    return generateValue("strike", "put");
  }

  def putVolume(): Long = {
    return generateValue("volume", "put").toLong
  }

  def callVolatility(): Double = {
    return generateValue("impliedVolatility", "call")
  }

  def putVolatility(): Double = {
    return generateValue("impliedVolatility", "put")
  }

  def forDate(indentifier: String = "call"): java.util.Date = {
    if (indentifier == "call")
      return new java.util.Date((generateValue("expiration", "call").toLong * 1000))
    else
      return new java.util.Date((generateValue("expiration",  "put").toLong * 1000))
  }

  def showAllAttributes(): Unit = {
    println("for " + identifyingName + "\n\tthe call price/strike/volatility/volume/expiration is " + this.call()
      + "/" + this.callStrike() + "/"
      + this.callVolatility() + "/" + this.callVolume() + "/" + this.forDate() + "\n\t\tthe put price/strike/volatility/volume/expiration is "
      + this.put() + "/" + this.putStrike() + "/" + this.putVolatility() + "/" + this.putVolume() + "/" + this.forDate("put")
      + "\n\t\t\tand the last closing price is " + Utilities.lastPrice(instrumentName))
  }
}

object UtilitiesAdv {
  def apply(exString: String) = new UtilitiesAdv(exString)
}

