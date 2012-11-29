package com.xorlev.simon.util

/**
 * 2012-11-28
 * @author Michael Rose <michael@fullcontact.com>
 */

object HttpCodeUtil {
  val codes = Map(
    200 -> "200 OK",
    400 -> "400 Bad Request",
    404 -> "404 Not Found",
    500 -> "500 Internal Server Error"
  )

  def lookupCode(code: Int): String = {
    codes(code)
  }
}