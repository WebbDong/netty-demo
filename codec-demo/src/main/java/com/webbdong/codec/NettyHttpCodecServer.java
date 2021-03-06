package com.webbdong.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author Webb Dong
 * @date 2021-08-16 9:04 PM
 */
public class NettyHttpCodecServer {

    private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);

    public static void main(String[] args) {
        final NettyHttpCodecServer server = new NettyHttpCodecServer();
        server.start(20003);
    }

    @SneakyThrows
    public void start(final int port) {
        final EventLoopGroup boss = new NioEventLoopGroup(1);
        final EventLoopGroup worker = new NioEventLoopGroup();
        final ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer() {
                        @Override
                        public void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    // Http ???????????????
                                    .addLast(new HttpResponseEncoder())
                                    // Http ???????????????
                                    .addLast(new HttpRequestDecoder())
                                    // ????????????????????????????????????????????????
                                    .addLast(new HttpObjectAggregator(1024 * 1024 * 8))
                                    .addLast(new ServerHandler());
                        }
                    });
            final ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private static class ServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            FullHttpRequest request = (FullHttpRequest) msg;
            handleRequest(request);

            StringBuilder sb = new StringBuilder();
            sb.append("<html>")
                    .append("<head>")
                    .append("</head>")
                    .append("<body>")
                    .append("<h3>success</h3>")
                    .append("</body>")
                    .append("</html>");
            writeHttpResponse(request, ctx, sb.toString());
            super.channelRead(ctx, msg);
        }

        private void handleRequest(FullHttpRequest request) {
            if (HttpMethod.GET.equals(request.method())) {
                handleGet(request);
            } else if (HttpMethod.POST.equals(request.method())) {
                handlePost(request);
            } else {
                System.out.println(request.method() + " method is not supported ,please change http method for get or post!");
            }
        }

        private void handleGet(FullHttpRequest request) {
            parseRequestParams(request.uri(), true);
        }

        private void handlePost(FullHttpRequest request) {
            switch (getContentType(request)) {
                case "application/json":
                    parseJson(request);
                    break;
                case "application/x-www-form-urlencoded":
                    parseFormData(request);
                    break;
                case "multipart/form-data":
                    parseMultipart(request);
                    break;
            }
        }

        private void parseMultipart(FullHttpRequest request) {
            HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, request);
            // ??????????????? multipart
            if (postRequestDecoder.isMultipart()) {
                // ?????? body ????????????
                List<InterfaceHttpData> bodyHttpData = postRequestDecoder.getBodyHttpDatas();
                bodyHttpData.forEach(data -> {
                    // ????????????????????????
                    InterfaceHttpData.HttpDataType httpDataType = data.getHttpDataType();
                    if (httpDataType.equals(InterfaceHttpData.HttpDataType.Attribute)) {
                        // ????????????????????????????????????
                        Attribute attribute = (Attribute) data;
                        try {
                            System.out.println("???????????????:" + attribute.getName() + ", ????????????:" + attribute.getValue());
                        } catch (IOException e) {
                            System.out.println("???????????????????????????,msg=" + e.getMessage());
                        }
                    } else if (httpDataType.equals(InterfaceHttpData.HttpDataType.FileUpload)) {
                        // ??????????????????????????????????????????
                        FileUpload fileUpload = (FileUpload) data;
                        // ????????????????????????
                        String filename = fileUpload.getFilename();
                        // ?????????????????????
                        String name = fileUpload.getName();
                        System.out.println("????????????:" + filename + ", ???????????????:" + name);
                        // ????????????????????????
                        if (fileUpload.isCompleted()) {
                            try {
                                String path = "D:\\" + filename;
//                                File file = fileUpload.getFile();
                                fileUpload.renameTo(new File(path));
                            } catch (IOException e) {
                                System.out.println("??????????????????,msg=" + e.getMessage());
                            }
                        }
                    }
                });
            }
        }

        private void parseFormData(FullHttpRequest request) {
            parseRequestParams(request.uri(), true);
            parseRequestParams(request.content().toString(StandardCharsets.UTF_8), false);
        }

        private void parseJson(FullHttpRequest request) {
            String json = request.content().toString(StandardCharsets.UTF_8);
            System.out.println("requestJson = " + json);
        }

        private void parseRequestParams(String uri, boolean hasPath) {
            // ?????? QueryStringDecoder ?????? Key/Value ?????????
            QueryStringDecoder qsd  = new QueryStringDecoder(uri, StandardCharsets.UTF_8, hasPath);
            Map<String, List<String>> parameters = qsd.parameters();
            // ??????????????????
            if (parameters != null && parameters.size() != 0) {
                parameters.entrySet().stream().forEach(entry ->
                        System.out.println("paramKey = " + entry.getKey() + ", paramValue = " + entry.getValue()));
            }
        }

        private String getContentType(FullHttpRequest request) {
            String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
            return contentType.split(";")[0];
        }

        private void writeHttpResponse(FullHttpRequest request, ChannelHandlerContext ctx, String html) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.content().writeBytes(html.getBytes(StandardCharsets.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
            HttpUtil.setContentLength(response, response.content().readableBytes());
            if (HttpUtil.isKeepAlive(request)) {
                HttpUtil.setKeepAlive(response, true);
                ctx.writeAndFlush(response);
            } else {
                ctx.writeAndFlush(response)
                        .addListener(ChannelFutureListener.CLOSE);
            }
        }

    }

}
