package cipher

class SessionKey {

  private[this] var sessionKey: String = _
  private[this] var creationTime: Long = _
  private[this] var destroyTime: Long = _


  def this(lifeTime: Long) = {

    this()

    this.sessionKey = AESCipher.generateKey()
    this.creationTime = System.currentTimeMillis()
    this.destroyTime = this.creationTime + lifeTime
  }


  def isAlive: Boolean = System.currentTimeMillis() < this.destroyTime


  def getSessionKey: String = {

    if (isAlive) sessionKey
    else null

  }



}
