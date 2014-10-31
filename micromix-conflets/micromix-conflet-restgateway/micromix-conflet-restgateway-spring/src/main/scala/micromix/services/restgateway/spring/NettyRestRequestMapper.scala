package micromix.services.restgateway.spring

import io.fabric8.process.spring.boot.actuator.camel.rest.{RestRequest, RestRequestMapper}

import scala.collection.JavaConversions._

class NettyRestRequestMapper extends RestRequestMapper[NettyRequest] {

  override def mapRequest(nettyRequest: NettyRequest): RestRequest = {
    val request = nettyRequest.request
    val headers = request.headers.iterator.foldLeft(Map[String, String]()) {
      (headersMap, incomingHeader) => headersMap + (incomingHeader.getKey -> incomingHeader.getValue)
    }
    val uriFragments = request.getUri.split("/")
    val service = uriFragments(2)
    val operation = uriFragments(3)
    val params = uriFragments.slice(4, uriFragments.length)
    new RestRequest(headers, service, operation, nettyRequest.channelContext.getChannel.getRemoteAddress, params: _*)
  }

}
