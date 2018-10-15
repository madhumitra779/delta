/*
 Delta Version 1.1.6 Options Prices Demonstrator
 Copyright (C) 2018  A. Madhur

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.

 This program comes with ABSOLUTELY NO WARRANTY.
 This is free software, and you are welcome to redistribute it
 under certain conditions; type delta.conditions() for details in terminal.
 */

import scala.math.{exp, Pi, abs}
import scala.collection.mutable.ArrayBuffer

object delta {
  def main(args: Array[String]): Unit = {
    //val w = new UtilitiesAdv("DWARKESH")
    val sp500List = scala.io.Source.fromFile("values.txt").getLines.toArray
    if(args.length == 0) {
      for(i <- 0 to 10) {
        val instrumentName = sp500List(i).toString
        try {
          tools.UtilitiesAdv(instrumentName).showAllAttributes
        } catch {
          case _: Throwable => println("Some Error has occurred for " + instrumentName)
        }
      }
    }

    else if(args.length == 1) {
      var stringOrInt: Boolean = false;
      try { args(0).toInt }
      catch {
        case numEx: java.lang.NumberFormatException => (stringOrInt = true);
      }
      if(stringOrInt == false) {
        for(i <- 0 to args(0).toInt-1) {
          try { tools.UtilitiesAdv(sp500List(i).toString).showAllAttributes }
          catch {
            case runEx: java.lang.RuntimeException => println("Parser failure encountered for " + sp500List(i).toString);
          }
        }
      }
      else {
        try { tools.UtilitiesAdv(args(0)).showAllAttributes }
        catch {
          case runEx: java.lang.RuntimeException => println("Parser failure encountered for " + args(0).toString);
        }
      }
    }
    else {
      for(i <- args) {
        val w = tools.UtilitiesAdv(i)
        w.showAllAttributes
      }
    }
  }

  def conditions(): Unit = {
    println("""Delta Version 1.1.6 Options Prices Demonstrator
    Copyright (C) 2018  A. Madhur

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    This program comes with ABSOLUTELY NO WARRANTY.
    This is free software, and you are welcome to redistribute it
    under certain conditions.""")
  }
}


