package com.xorlev.simon.util

import management.ManagementFactory

/**
 * 2012-12-02
 * @author Michael Rose <elementation@gmail.com>
 */

object JmxUtil {
  def getPid: Int = {
    ManagementFactory.getRuntimeMXBean.getName.split('@')(0).toInt
  }
}