package micromix.services.restgateway.spring

import micromix.conflet.restgateway.FixedTokenAuthGatewayInterceptor
import org.apache.camel.{Exchange, Processor}

/**
  * Copyright (C) Coderion sp. z o.o.
  */
class SecurityPipelineProcessor extends Processor {
  override def process(exchange: Exchange) {
    exchange.getIn.setHeader("Access-Control-Allow-Origin", "*")
    exchange.getIn.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, " +
      FixedTokenAuthGatewayInterceptor.tokenHeader + ", " + FixedTokenAuthGatewayInterceptor.plainApiHeader)
    exchange.getIn.setHeader("X-Frame-Options", "SAMEORIGIN")
    exchange.getIn.setHeader("X-XSS-Protection", "1; mode=block")
    exchange.getIn.setHeader("Content-Security-Police", "default-src 'self' www.google-analytics.com ajax.googleapis.com")
    exchange.getIn.setHeader("X-Content-Type-Options", "nosniff")
  }
}
