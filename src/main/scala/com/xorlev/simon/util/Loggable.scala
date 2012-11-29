package com.xorlev.simon.util

import org.slf4j.LoggerFactory

/**
 * 2012-11-28
 * @author Michael Rose <michael@fullcontact.com>
 */

trait Loggable {
  protected lazy val log = LoggerFactory.getLogger(this.getClass)
}