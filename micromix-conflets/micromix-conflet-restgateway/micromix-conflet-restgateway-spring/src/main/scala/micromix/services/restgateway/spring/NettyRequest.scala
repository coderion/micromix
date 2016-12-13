package micromix.services.restgateway.spring

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpRequest

case class NettyRequest(request: HttpRequest, channelContext: ChannelHandlerContext)
