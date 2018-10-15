package tools
import scala.math.{abs, sqrt, exp, Pi, log}
import org.apache.commons.math3.distribution.{NormalDistribution}
import org.apache.commons.math3.analysis.differentiation._
import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

object Utilities {
  def maxElement(arrayInput: Array[Double]): Double = {
    val maxOne = arrayInput.reduceLeft(_ max _).toDouble
    return maxOne
  }

  /* this is the familiar numpy's routine np.linspace;
     implemented in scala.
   */
  def linspace(initialPoint: Double, finalPoint: Double, divisions: Double):
      Array[Double] = {
    val dx = ArrayBuffer[Double]()
    val limited: Double = (finalPoint - initialPoint)/(divisions-1)
    var i: Double = initialPoint
    while(i < finalPoint) {
      dx += i
      i = i + limited
    }
    if(dx.size + 1 == divisions.toInt)
      dx += (finalPoint)
    return dx.toArray
  }

  def valuesWriter(inputArray: Array[Double],
    fileName: String = "values"): Unit = {
    val w = scala.collection.mutable.ArrayBuffer[String]()
    inputArray.foreach(element => w+=(element.toString + "\n"))
    scala.tools.nsc.io.File(fileName+".csv")
      .writeAll(w.toArray.mkString)
  }

  /* last price of a financial instrument; mainly for equities */
  def lastPrice(instrumentName: String): Double = {
    val theURL = "https://finance.yahoo.com/quote/"+instrumentName+"?p="+instrumentName
    val inThisString = scala.io.Source.fromURL(theURL).mkString
    val findings = "\"regularMarketPrice\":\\{\"raw\":([0-9.]+),".r
      .findAllIn(inThisString).toArray
    val lastPx = findings(0)
    return "([0-9.]+)".r.findAllIn(lastPx).toArray.mkString.toDouble
  }

  def derivativeEngine(f: Double => Double, x: Double): Double = {
    // this is not the production grade differentiation code
    // please see the newer versions of the boost::algo or boost::numerics
    val dx: Double = 1.0E-9;
    val dx1 = dx;
    val dx2 = dx1 * 2;
    val dx3 = dx1 * 3;

    val m1 = (f(x + dx1) - f(x - dx1)) / 2;
    val m2 = (f(x + dx2) - f(x - dx2)) / 4;
    val m3 = (f(x + dx3) - f(x - dx3)) / 6;

    val fifteen_m1 = 15 * m1;
    val six_m2         = 6   * m2;
    val ten_dx1       = 10 * dx1;

    return ((fifteen_m1 - six_m2) + m3) / ten_dx1;
  }

  def differentiation(indexNumber: Int, vOne: Array[Double],
    xOne: Array[Double]): Double = {
    val fx: Double = vOne(indexNumber-1) - vOne(indexNumber)
    val dx: Double = xOne(indexNumber-1) - xOne(indexNumber)
    return fx/dx
  }

  def firstOrderDifferentiation(vOne: Array[Double], n: Int):
      Array[Double] = {
    val xAxis = linspace(0.0, n*Pi, vOne.size.toDouble)
    val vDash = ArrayBuffer[Double]()
    var i: Int = 1
    while(i < vOne.size) {
      vDash += differentiation(i, vOne, xAxis)
      i = i + 1
    }
    return vDash.toArray
  }

  def secondOrderDifferentiation(vOne: Array[Double], n: Int):
      Array[Double] = {
    val vDoubleDash = firstOrderDifferentiation(vOne, n)
    return firstOrderDifferentiation(vDoubleDash, n)
  }

  def pdf(x: Double): Double = {
    val w = new NormalDistribution()
    return w.density(x)
  }

  def cdf(x: Double): Double = {
    val w = new NormalDistribution()
    return w.cumulativeProbability(x)
  }

  def cdfN(x: Double): Double = {
  // Abramovich and Stengun Formula 7.1.26
    val a1: Double =   0.254829592
    val a2: Double = -0.284496736
    val a3: Double =   1.421413741
    val a4: Double = -1.453152027
    val a5: Double =   1.061405429
    val p : Double =   0.3275911

    var sign: Int = 1
    if(x < 0)
      sign = -1
    val z: Double = abs(x)/sqrt(2.0)
    val t: Double = 1.0/(1.0 + p*z)
    val y: Double = 1.0 - (((((a5*t + a4)*t) + a3)*t + a2)*t + a1)*t*exp(-z*z)

    return 0.5*(1.0 + sign*y)
  }

  class OptionsAux {
    private var currentPrice = 0.0
    private var strikePrice = 0.0
    private var riskFreeRate = 0.0
    private var impliedVolatility = 0.0
    private var totalTime = 0.0
    def this(currentPrice: Double, strikePrice: Double, riskFreeRate: Double, impliedVolatility: Double,
      totalTime: Double) {
      this()
      this.currentPrice = currentPrice
      this.strikePrice = strikePrice
      this.riskFreeRate = riskFreeRate/100.0
      this.impliedVolatility = impliedVolatility
      this.totalTime = totalTime/365.0
    }

    def dOne(): Double = {
      val parantheses: Double = log(currentPrice/strikePrice) + ((riskFreeRate
        + (impliedVolatility*impliedVolatility)*0.5)*(totalTime))
      val outerValue: Double = 1.0/(impliedVolatility * sqrt(totalTime))
      return outerValue*parantheses
    }

    def dTwo(): Double = {
      val valueOfdOne: Double = dOne()
      val valueOfRemaining: Double = impliedVolatility*sqrt(totalTime)
      return valueOfdOne - valueOfRemaining
    }

    def callPrice(): Double = {
      val valueOfdOne: Double = cdf(dOne())
      val valueOfdTwo: Double = cdf(dTwo())
      val firstTerm: Double = valueOfdOne*currentPrice
      val secondTerm: Double = valueOfdTwo*strikePrice*exp(-riskFreeRate*totalTime)
      return firstTerm - secondTerm
    }

    def putPrice(): Double = {
      val valueOfdOne: Double = cdf(-1.0*dOne())
      val valueOfdTwo: Double = cdf(-1.0*dTwo())
      val firstTerm: Double = valueOfdTwo*strikePrice*exp(-riskFreeRate*totalTime)
      val secondTerm: Double = valueOfdOne*currentPrice
      return firstTerm - secondTerm
    }

    def delta(identifier: String): Double = {
      if(identifier == "C" || identifier == "c" || identifier == "call" ||
        identifier == "Call")
        return cdf(dOne())
      else
        return -1.0*cdf(-1.0 * dOne())
    }

    def dualDelta(identifier: String): Double = {
      val multiplier: Double = exp(-1.0*riskFreeRate*totalTime)
      if(identifier == "C" || identifier == "c" || identifier == "call" ||
        identifier == "Call") {
        return -1.0*multiplier*cdf(dTwo())
      }
      else
        return multiplier*cdf(-1.0*dTwo())
    }

    def vega(): Double = {
      return currentPrice*pdf(dOne())*sqrt(totalTime)/100.0
    }

    def theta(identifier: String): Double = {
      val mux: Double = exp(-1.0*riskFreeRate*totalTime)
      val w: Double = -currentPrice*pdf(dOne())*impliedVolatility*2.0*sqrt(totalTime)
      val z: Double = 2.0*sqrt(totalTime)
      val y: Double = strikePrice*mux*cdf(dTwo)*riskFreeRate
      if(identifier == "C" || identifier == "c" || identifier == "call" ||
        identifier == "Call") {
        return (w/z) - y
      }
      else {
        return (w/z) + y
      }
    }

    def rho(identifier: String): Double = {
      val mux: Double = exp(-1.0*riskFreeRate*totalTime)
      if(identifier == "C" || identifier == "c" || identifier == "call" ||
        identifier == "Call") {
        return strikePrice*totalTime*mux*cdf(dTwo())/100.0
      }
      else {
        return -1.0*strikePrice*totalTime*mux*cdf(-1.0*dTwo())/100.0
      }
    }

    def gamma(): Double = {
      val aux: Double = impliedVolatility * sqrt(totalTime)
      return pdf(dOne())/(currentPrice*aux)
    }

    def parity(): Double = {
      val firstTerm: Double = callPrice() - putPrice() - currentPrice + strikePrice
      val secondTerm: Double = scala.math.pow((1.0 + riskFreeRate), totalTime)
      return firstTerm / secondTerm
    }
    
  }

  def generateRandomNumbers(lowerLimit: Double, upperLimit: Double, numberOfNumbers: Int): Array[Double] = {
    val randomGenerator = scala.util.Random
    val w = ArrayBuffer[Double]()
    for(i <- 1 to numberOfNumbers) {
      w += (lowerLimit.toInt + randomGenerator.nextInt((upperLimit.toInt - lowerLimit.toInt)
        + 1) + randomGenerator.nextDouble)
    }
    return w.toArray
  }

  import plotter._
  def plotArray(array: ArrayBuffer[Double], title: String): Unit = {
    new plotter.plotter(title, array).main(Array())
  }

  // similar to that of python's len() function. for debug/analyse purposes in sbt console
  def len[Q: scala.reflect.ClassTag](inputArray: Array[Q]): Int = { return inputArray.length; }
}
