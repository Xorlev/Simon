package com.xorlev.simon.handlers

import com.xorlev.simon.util.SimonException

/**
 * 2012-12-13
 * @author Michael Rose <elementation@gmail.com>
 */

case class HaltedHandlerException(code: Int, haltMessage: String = "") extends SimonException