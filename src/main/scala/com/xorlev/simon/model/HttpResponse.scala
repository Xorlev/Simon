package com.xorlev.simon.model

import java.io.InputStream

/**
 * 2012-11-28
 * @author Michael Rose <michael@fullcontact.com>
 */
case class HttpResponse(responseCode: Int, mimeType:String, response:InputStream)