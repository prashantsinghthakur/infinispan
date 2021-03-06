package org.infinispan.server.hotrod

import org.infinispan.stats.Stats
import org.infinispan.server.core.QueryFacade
import org.infinispan.server.core.transport.NettyTransport
import org.infinispan.container.entries.CacheEntry
import io.netty.buffer.ByteBuf
import org.infinispan.server.hotrod.configuration.HotRodServerConfiguration
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.Channel

/**
 * This class represents the work to be done by a decoder of a particular Hot Rod protocol version.
 *
 * @author Galder Zamarreño
 * @since 4.1
 */
abstract class AbstractVersionedDecoder {

   /**
    * Having read the message's Id, read the rest of Hot Rod header from the given buffer and return it.
    */
   def readHeader(buffer: ByteBuf, version: Byte, messageId: Long, header: HotRodHeader): Boolean

   /**
    * Read the key to operate on from the message.
    */
   def readKey(header: HotRodHeader, buffer: ByteBuf): (Array[Byte], Boolean)

   /**
    * Read the parameters of the operation, if present.
    */
   def readParameters(header: HotRodHeader, buffer: ByteBuf): (RequestParameters, Boolean)

   /**
    * Create a successful response.
    */
   def createSuccessResponse(header: HotRodHeader, prev: Array[Byte]): Response

   /**
    * Create a response indicating the the operation could not be executed.
    */
   def createNotExecutedResponse(header: HotRodHeader, prev: Array[Byte]): Response

   /**
    * Create a response indicating that the key, which the message tried to operate on, did not exist.
    */
   def createNotExistResponse(header: HotRodHeader): Response

   /**
    * Create a response for get a request.
    */
   def createGetResponse(header: HotRodHeader, entry: CacheEntry[Array[Byte], Array[Byte]]): Response

   /**
    * Handle a protocol specific header reading.
    */
   def customReadHeader(header: HotRodHeader, buffer: ByteBuf,
       cache: Cache, server: HotRodServer, ctx: ChannelHandlerContext): AnyRef

   /**
    * Handle a protocol specific key reading.
    */
   def customReadKey(decoder: HotRodDecoder, header: HotRodHeader, buffer: ByteBuf, cache: Cache, server: HotRodServer, ch: Channel): AnyRef

   /**
    * Handle a protocol specific value reading.
    */
   def customReadValue(decoder: HotRodDecoder, header: HotRodHeader, hrCtx: CacheDecodeContext, buffer: ByteBuf, cache: Cache): AnyRef

   /**
    * Create a response for the stats command.
    */
   def createStatsResponse(header: HotRodHeader, stats: Stats, t: NettyTransport): StatsResponse

   /**
    * Create an error response based on the Throwable instance received.
    */
   def createErrorResponse(header: HotRodHeader, t: Throwable): ErrorResponse

   /**
    * Get an optimized cache instance depending on the operation parameters.
    */
   def getOptimizedCache(h: HotRodHeader, c: Cache): Cache

}
