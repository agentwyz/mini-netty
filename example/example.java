/*
*template code of netty
*/
public class LimServer {
    private final static Logger logger = LoggerFactory.getLogger(LimServer.class);

    BootStrapConfig.TcpConfig config;
    EventLoopGroup mainGroup;
    EventLoopGroup subGroup;
    ServerBootstrap server;

    public LimServer(BootStrapConfig.TcpConfig config) {
        this.config = config;


        mainGroup = new NioEventLoopGroup(config.getBossThreadSize());
        subGroup = new NioEventLoopGroup(config.getWorkThreadSize());

        server = new ServerBootstrap();

        server.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 10240)        //服务端可连接队列大小
                .option(ChannelOption.SO_REUSEADDR, true)       //参数表示允许重复使用本地地址和端口
                .childOption(ChannelOption.TCP_NODELAY, true)  //是否禁用Nagle算法, 简单点说是否批量发送数据
                .childOption(ChannelOption.SO_KEEPALIVE, true) //保活开关2h没有数据服务端会发送心跳包
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        socketChannel.pipeline().addLast(new MessageDecoder());
                        socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, 3));
                        socketChannel.pipeline().addLast(new HearBeatHandler(config.getHearBeatTime()));
                        socketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                });
    }


    public void start() {
        this.server.bind(this.config.getTcpPort());
    }

}
