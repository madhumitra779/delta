package plotter
import scala.collection.mutable.{ListBuffer, ArrayBuffer}
import scalafx.application
import scalafx.application.JFXApp
import scalafx.geometry.Side
import scalafx.scene.Scene
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.collections.ObservableBuffer
import scalafx.css._

class plotter(name: String, w: ArrayBuffer[Double]) extends JFXApp {

  def xySeries(data: Seq[(Double, Double)]) = {
    XYChart.Series[Number, Number](
      ObservableBuffer(data.map { case (x, y) =>
        XYChart.Data[Number, Number](x, y)})
    )
  }

  val x = ArrayBuffer[Double]()
  for(i <- 0 to w.length-1) {
    x += (i.toDouble)
  }

  if(x.length == w.length) {
    var wOne = ListBuffer[(Double, Double)]()
    var iOne: Int = 0
    while(iOne < w.length) {
      wOne +=((x(iOne), w(iOne)))
      iOne = iOne + 1
    }
    val seqList = wOne.toSeq
    val seqData = xySeries(seqList)

    stage = new application.JFXApp.PrimaryStage {
      title = name
      scene = new Scene {
        root = new LineChart(NumberAxis("TimeAxis"), NumberAxis("Values")) {
          title = name
          legendSide = Side.Right
          data = ObservableBuffer(seqData)
          stylesheets.add("lineStyle.css")
        }
      }
    }
  }
  else {
    println("Lenghts don't match")
  }
}

