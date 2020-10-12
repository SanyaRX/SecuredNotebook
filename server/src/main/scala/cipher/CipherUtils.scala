package cipher


import scala.util.Random


object CipherUtils {

  def generateStringKey(length: Int): String = {
    Random.alphanumeric.take(length).mkString
  }

}
