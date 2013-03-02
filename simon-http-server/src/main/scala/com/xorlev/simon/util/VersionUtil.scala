/*
 * Copyright (c) 2012 Michael Rose
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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