package com.webbdong.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

/**
 * ByteBuf
 * @author Webb Dong
 * @date 2021-08-10 10:29 PM
 */
public class ByteBufDemo {

    public static void main(String[] args) {
//        read();
//        write();
//        discard();
//        wrap();
//        unpooledByteBuf();
//        byteBufAllocator();
//        release();
        other();
    }

    public static void read() {
        ByteBuf buf = Unpooled.copiedBuffer("Hello Netty".getBytes(StandardCharsets.UTF_8));
        System.out.println("ByteBuf 容量为 " + buf.capacity());
        System.out.println("ByteBuf 最大容量为 " + buf.maxCapacity());
        System.out.println("ByteBuf 可读容量为 " + buf.readableBytes());
        System.out.println("ByteBuf 可写容量为 " + buf.writableBytes());

        // 按字节逐个读取
        while (buf.isReadable()) {
            // buf.readByte() 会改变 readerIndex 读取索引
            System.out.print(Character.valueOf((char) buf.readByte()));
        }
        System.out.println();

        // 由于上面的循环已经读取完成了，需要重置读取索引，下面的循环才能重新读取
        buf.resetReaderIndex();
        // 通过下标逐个读取
        for (int i = 0; i < buf.readableBytes(); i++) {
            // 使用 buf.getByte 方式读取时，不会改变 readerIndex 读取索引
            System.out.print(Character.valueOf((char) buf.getByte(i)));
        }
        System.out.println();

        // 读取到字节数组
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
    }

    public static void write() {
        // 构造 ByteBuf, 指定初始容量和最大容量
        ByteBuf buf = Unpooled.buffer(10, 1024);

        // 每次写入1个字节
        for (int i = 0; i < 100; i++) {
            buf.writeByte(i);
        }
        System.out.println("ByteBuf 容量为 " + buf.capacity());
        System.out.println("ByteBuf 最大容量为 " + buf.maxCapacity());
        System.out.println("ByteBuf 可读容量为 " + buf.readableBytes());
        System.out.println("ByteBuf 可写容量为 " + buf.writableBytes());
        System.out.println();

        byte[] bytes = "hello netty".getBytes(StandardCharsets.UTF_8);
        buf.resetWriterIndex();
        // 直接将 byte 数组中的数据写入 ByteBuf
        buf.writeBytes(bytes);
        System.out.println("ByteBuf 容量为 " + buf.capacity());
        System.out.println("ByteBuf 最大容量为 " + buf.maxCapacity());
        System.out.println("ByteBuf 可读容量为 " + buf.readableBytes());
        System.out.println("ByteBuf 可写容量为 " + buf.writableBytes());

        byte[] bytes2 = new byte[buf.readableBytes()];
        buf.readBytes(bytes2);
        System.out.println(new String(bytes2, StandardCharsets.UTF_8));
    }

    public static void discard() {
        ByteBuf buf = Unpooled.copiedBuffer("hello netty".getBytes(StandardCharsets.UTF_8));
        System.out.println("ByteBuf 容量为 " + buf.capacity());
        System.out.println("ByteBuf 最大容量为 " + buf.maxCapacity());
        System.out.println("ByteBuf 可读容量为 " + buf.readableBytes());
        System.out.println("ByteBuf 可写容量为 " + buf.writableBytes());
        System.out.println("ByteBuf 读索引 " + buf.readerIndex());
        System.out.println("ByteBuf 写索引 " + buf.writerIndex());
        System.out.println();

        for (int i = 0; i < 5; i++) {
            buf.readByte();
        }

        System.out.println("ByteBuf 容量为 " + buf.capacity());
        System.out.println("ByteBuf 最大容量为 " + buf.maxCapacity());
        System.out.println("ByteBuf 可读容量为 " + buf.readableBytes());
        System.out.println("ByteBuf 可写容量为 " + buf.writableBytes());
        System.out.println("ByteBuf 读索引 " + buf.readerIndex());
        System.out.println("ByteBuf 写索引 " + buf.writerIndex());
        System.out.println();

        // 丢弃已经读取的字节，可写空间变多
        buf.discardReadBytes();

        System.out.println("ByteBuf 容量为 " + buf.capacity());
        System.out.println("ByteBuf 最大容量为 " + buf.maxCapacity());
        System.out.println("ByteBuf 可读容量为 " + buf.readableBytes());
        System.out.println("ByteBuf 可写容量为 " + buf.writableBytes());
        System.out.println("ByteBuf 读索引 " + buf.readerIndex());
        System.out.println("ByteBuf 写索引 " + buf.writerIndex());
        System.out.println();

        // clear 将 readerIndex = writerIndex = 0 只是指针变化,数据并没有清除,支持继续写入
        buf.clear();

        System.out.println("ByteBuf 容量为 " + buf.capacity());
        System.out.println("ByteBuf 最大容量为 " + buf.maxCapacity());
        System.out.println("ByteBuf 可读容量为 " + buf.readableBytes());
        System.out.println("ByteBuf 可写容量为 " + buf.writableBytes());
        System.out.println("ByteBuf 读索引 " + buf.readerIndex());
        System.out.println("ByteBuf 写索引 " + buf.writerIndex());
        System.out.println();

        // 释放
//        buf.release();
        ReferenceCountUtil.release(buf);

        System.out.println("ByteBuf 容量为 " + buf.capacity());
        System.out.println("ByteBuf 最大容量为 " + buf.maxCapacity());
        System.out.println("ByteBuf 可读容量为 " + buf.readableBytes());
        System.out.println("ByteBuf 可写容量为 " + buf.writableBytes());
        System.out.println("ByteBuf 读索引 " + buf.readerIndex());
        System.out.println("ByteBuf 写索引 " + buf.writerIndex());
    }

    public static void wrap() {
        byte[] bytes = "123456".getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        // 使用 wrappedBuffer 创建的 ByteBuf，修改原始数据的 bytes 数组后，ByteBuf 也会被修改
        bytes[0] = 1;
        System.out.println("原始数据 " + new String(bytes, StandardCharsets.UTF_8));
        System.out.println("buf中的数据 " + buf.toString(StandardCharsets.UTF_8));
        System.out.println();

        ByteBuf buf2 = new UnpooledHeapByteBuf(UnpooledByteBufAllocator.DEFAULT, 10, 100);
        buf2.writeBytes("Hello".getBytes(StandardCharsets.UTF_8));
        System.out.println(buf2.getClass().getTypeName());
    }

    /**
         ByteBuf 三种模式:
             1. 堆缓冲区（HeapByteBuf）: 内存分配在jvm堆，分配和回收速度比较快，可以被JVM自动回收，缺点是，如果进行 socket的IO读写，
                需要额外做一次内存复制，将堆内存对应的缓冲区复制到内核Channel中，性能会有一定程度的下 降。由于在堆上被 JVM 管理，
                在不被使用时可以快速释放。可以通过 ByteBuf.array() 来获取 byte[] 数据。

             2. 直接缓冲区（DirectByteBuf）: 内存分配的是堆外内存（系统内存），相比堆内存，它的分配和回收速度会慢一些，
                但是将它写入或从Socket Channel中读取时，由于减少了一次内存拷贝，速度比堆内存块。

             3. 复合缓冲区（CompositeByteBuf）: 将两个不同的缓冲区从逻辑上合并，让使用更加方便。

             Netty默认使用的是 DirectByteBuf，如果需要使用 HeapByteBuf 模式，则需要进行系统参数的设置。

             // 设置 HeapByteBuf 模式，但 ByteBuf 的分配器 ByteBufAllocator 要设置为非池化，否则不能切换到堆缓冲模式
             System.setProperty("io.netty.noUnsafe", "true");
     */
    public static void unpooledByteBuf() {
        // 堆缓冲区
        ByteBuf buf1 = new UnpooledHeapByteBuf(UnpooledByteBufAllocator.DEFAULT, 100, 1024);
        buf1.writeBytes("UnpooledHeapByteBuf".getBytes(StandardCharsets.UTF_8));

        // 直接缓冲区
        ByteBuf buf2 = new UnpooledDirectByteBuf(UnpooledByteBufAllocator.DEFAULT, 100, 1024);
        buf2.writeBytes("UnpooledDirectByteBuf".getBytes(StandardCharsets.UTF_8));

        // 复合缓冲区
        ByteBuf buf3 = new CompositeByteBuf(UnpooledByteBufAllocator.DEFAULT, false,
                16, new ByteBuf[] {buf1, buf2});
        System.out.println("buf3 = " + buf3.toString(StandardCharsets.UTF_8));

        CompositeByteBuf buf4 = Unpooled.compositeBuffer();
        // increaseWriterIndex 参数指定添加 buf 时，是否更新写索引，默认情况下是 false 不更新写索引，将无法读取
        buf4.addComponents(true, buf1, buf2);
        System.out.println("buf4 = " + buf4.toString(StandardCharsets.UTF_8));
    }

    /**
        ByteBuf 的分配器，Netty 提供了两种 ByteBufAllocator 的实现
            1. PooledByteBufAllocator : 实现了 ByteBuf 的对象的池化，提高性能减少并最大限度地减少内存碎片，
                池化思想通过预先申请一块专用内存地址作为内存池进行管理，从而不需要每次都进行分配和释放

            2. UnpooledByteBufAllocator : 没有实现对象的池化，每次会生成新的对象实例

            Netty 默认使用了 PooledByteBufAllocator，但可以通过引导类设置非池化模式

            // 方式一 : 引导类中设置非池化模式
            bootstrap.childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
            // 方式二 : 通过系统参数设置
            System.setProperty("io.netty.allocator.type", "pooled");
            System.setProperty("io.netty.allocator.type", "unpooled");

        Pooled & Unpooled
            1. 对于 Pooled 类型的 ByteBuf，不管是 PooledDirectByteBuf 还是 PooledHeapByteBuf 都只能由 Netty 内部自己使用（构造是私有和受保护的），
                开发者可以使用 Unpooled 类型的 ByteBuf。
            2. Netty 提供 Unpooled 工具类创建的 ByteBuf 都是 unpooled 类型，默认采用的 Allocator 是 direct 类型；
                当然用户可以自己选择创建 UnpooledDirectByteBuf 和 UnpooledHeapByteBuf
     */
    public static void byteBufAllocator() {
    }

    /**
        ByteBuf 的释放
            ByteBuf如果采用的是堆缓冲区模式的话，可以由 GC 回收，但是如果采用的是直接缓冲区，就不受 GC 管理，就得手动释放，
            否则会发生内存泄露，Netty 自身引入了引用计数，提供了 ReferenceCounted 接口，当对象的引用计数 > 0 时要保证对象不被释放，
            当为 0 时需要被释放

        关于 ByteBuf 的释放，分为手动释放与自动释放:
            手动释放
                在使用完成后，调用 ReferenceCountUtil.release(byteBuf); 进行释放，这种方式的弊端就是一旦忘记释放就可能会造成内存泄露

            自动释放
                自动释放有三种方式，分别是：入站的TailHandler（TailContext）、继承SimpleChannelInboundHandler、HeadHandler（HeadContext）的出站释放

                1. TailContext
                    Inbound 流水线的末端，如果前面的 handler 都把消息向后传递最终由 TailContext 释放该消息，需要注意的是，
                    如果没有进行向下传递，是不会进行释放操作的

                2. SimpleChannelInboundHandler
                    自定义的 InboundHandler 继承自 SimpleChannelInboundHandler，在 SimpleChannelInboundHandler 中自动释放

                3. HeadContext
                    Outbound 流水线的末端，出站消息一般是由应用所申请，到达最后一站时，经过一轮复杂的调用，在 flush 完成后终将被释放掉

        总结
            对于入站消息
                1. 对原消息不做处理，依次调用 ctx.fireChannelRead(msg) 把原消息往下传，如果能到 TailContext，那不用做什么释放，它会自动释放
                2. 将原消息转化为新的消息并调用 ctx.fireChannelRead(newMsg) 往下传，那需要将原消息 release 掉
                3. 如果已经不再调用 ctx.fireChannelRead(msg) 传递任何消息，需要把原消息 release 掉。

            对于出站消息
                无需用户关心，消息最终都会走到 HeadContext，flush 之后会自动释放。
     */
    public static void release() {
        byte[] bytes = "ByteBuf".getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.directBuffer();
        buf.writeBytes(bytes);
        System.out.println(buf.toString(StandardCharsets.UTF_8));
//        buf.release();
        ReferenceCountUtil.release(buf);
    }

    public static void other() {
        ByteBuf buf1 = Unpooled.wrappedBuffer("Hello".getBytes(StandardCharsets.UTF_8));
        // 切割出4个字节的 ByteBuf，调用了 readRetainedSlice 的 ByteBuf 会变更读索引
        ByteBuf sliceBuf = buf1.readRetainedSlice(4);
        System.out.println("sliceBuf = " + sliceBuf.toString(StandardCharsets.UTF_8));
        System.out.println("buf1 = " + buf1.toString(StandardCharsets.UTF_8));

        ByteBuf buf2 = Unpooled.wrappedBuffer("Ferrari".getBytes(StandardCharsets.UTF_8));
        // 从头开始忽略掉3个字节
        buf2.skipBytes(3);
        System.out.println("buf2 = " + buf2.toString(StandardCharsets.UTF_8));

        ByteBuf buf3 = Unpooled.wrappedBuffer("Lamborghini$".getBytes(StandardCharsets.UTF_8));
        // 查找某个字节在 ByteBuf 中的索引位置
        int index = buf3.forEachByte(new ByteProcessor.IndexOfProcessor((byte) '$'));
        System.out.println("index = " + index);
        System.out.println("buf3 = " + buf3.toString(StandardCharsets.UTF_8));
        System.out.println((char) buf3.getByte(index));
    }

}
