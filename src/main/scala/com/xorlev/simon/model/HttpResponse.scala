package com.xorlev.simon.model

import java.io.InputStream
import com.xorlev.simon.RequestParser.HeaderLine

/**
 * 2012-11-28
 * @author Michael Rose <elementation@gmail.com>
 */
case class HttpResponse(responseCode: Int, mimeType:String, response:InputStream, extraHeaders:List[HeaderLine] = List())