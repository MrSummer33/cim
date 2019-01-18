package com.crossoverjie.cim.server.init;

import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import com.crossoverjie.cim.server.handle.CIMServerHandle;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie Date: 17/05/2018 18:51
 * @since JDK 1.8
 */
public class CIMServerInitializer extends ChannelInitializer<Channel> {

    private final CIMServerHandle cimServerHandle = new CIMServerHandle();

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ch.pipeline()
                // google Protobuf 编解码
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(CIMRequestProto.CIMReqProtocol.getDefaultInstance()) {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
                            throws Exception {
                        if (msg instanceof ByteBuf) {
                            ByteBuf buf = ((ByteBuf) msg).retain();
                            String str;
                            if (buf.hasArray()) { // 处理堆缓冲区
                                str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(),
                                        buf.readableBytes());
                            } else { // 处理直接缓冲区以及复合缓冲区
                                byte[] bytes = new byte[buf.readableBytes()];
                                buf.getBytes(buf.readerIndex(), bytes);
                                str = new String(bytes, 0, buf.readableBytes(), "UTF-8");
                            }
                            System.out.println(str);
                        }
                        super.decode(ctx, msg, out);
                    }
                })
//                .addLast(new ProtobufDecoder(CIMRequestProto.CIMReqProtocol.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(cimServerHandle);
    }
}
