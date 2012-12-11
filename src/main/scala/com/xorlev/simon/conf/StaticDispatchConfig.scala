package com.xorlev.simon.conf

import com.xorlev.simon.RequestMapper
import com.xorlev.simon.handlers.TestHandler

/**
 * 2012-12-10
 * @author Michael Rose <michael@fullcontact.com>
 */

class StaticDispatchConfig extends DispatchConfig {
  def config() {
    //RequestMapper.registerHandler("/", new StaticFileRequestHandler("/", "_site/"))
    RequestMapper.registerHandler("/", new TestHandler("/"))
  }
}