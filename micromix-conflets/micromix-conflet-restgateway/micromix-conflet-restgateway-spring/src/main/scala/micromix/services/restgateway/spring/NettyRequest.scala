package micromix.services.restgateway.spring

import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.handler.codec.http.HttpRequest

case class NettyRequest(request: HttpRequest, channelContext: ChannelHandlerContext)
