package com.xorlev.simon.util

import org.slf4j.LoggerFactory

/**
 * 2012-11-28
 * @author Michael Rose <michael@fullcontact.com>
 */

trait Loggable {
  val log = LoggerFactory.getLogger(this.getClass)
}