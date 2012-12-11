package com.xorlev.simon.util

/**
 * 2012-11-28
 * @author Michael Rose <elementation@gmail.com>
 */

object VersionUtil {
  val version = (0,0,1)

  def getVersionString: String = version._1 + "." + version._2 + "." + version._3
  def getVersionMajor: Int = version._1
  def getVersionMinor: Int = version._2
  def getVersionPatch: Int = version._3
  def getServerString: String = "Simon/" + getVersionString
}