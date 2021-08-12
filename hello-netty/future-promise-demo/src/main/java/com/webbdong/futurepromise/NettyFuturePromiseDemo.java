package com.webbdong.futurepromise;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

/**
 * Future / Promise 异步模型
 *      future 和 promise，目的是将值（future）与其计算方式（promise）分离，从而允许更灵活地进行计算，特别是通过并行化。
 *      Future 表示目标计算的返回值，Promise 表示计算的方式，这个模型将返回结果和计算逻辑分离，目的是为了让计算逻辑不影响返回结果，
 *      从而抽象出一套异步编程模型。而计算逻辑与结果关联的纽带就是 callback
 *
 * @author Webb Dong
 * @date 2021-08-12 1:07 PM
 */
public class NettyFuturePromiseDemo {

    public static void main(String[] args) {
//        nettyFuture();
        nettyPromise();
    }

    /**
     * Netty Future 在 JDK 的 Future 基础上增加了监听器机制
     */
    @SneakyThrows
    public static void nettyFuture() {
        EventLoopGroup group = new NioEventLoopGroup();
        Future<String> future = group.submit(() -> {
            System.out.println("异步任务执行");
            TimeUnit.SECONDS.sleep(3);
            return "Hello Netty Future";
        });
        // 添加回调监听
        future.addListener(f -> System.out.println("异步任务回调执行 : " + f.get()));
        System.out.println("-------主线程-------");
        TimeUnit.SECONDS.sleep(5);
        group.shutdownGracefully();
    }

    /**
     * Promise 可以自由的控制异步处理结果与回调时机
     */
    @SneakyThrows
    public static void nettyPromise() {
        EventLoopGroup group = new NioEventLoopGroup();
        // promise 绑定到 EventLoop 上
        Promise promise = new DefaultPromise(group.next());
        group.submit(() -> {
            System.out.println("异步任务执行");
            try {
                TimeUnit.SECONDS.sleep(3);
                promise.setSuccess("Hello Netty Promise");
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
        });
        // 添加回调监听
        promise.addListener(f -> System.out.println("异步任务回调执行 : " + f.get()));
        System.out.println("-------主线程-------");
        TimeUnit.SECONDS.sleep(5);
        group.shutdownGracefully();
    }

}
