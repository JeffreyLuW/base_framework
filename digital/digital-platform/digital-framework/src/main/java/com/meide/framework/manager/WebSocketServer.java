package com.meide.framework.manager;

import com.alibaba.fastjson.JSONObject;
import com.meide.common.constant.HttpStatus;
import com.meide.common.constant.WebSocketConstants;
import com.meide.common.core.domain.WebsocketResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务层
 *
 * @author jiay
 */
@ServerEndpoint("/websocket/{openId}")
@Component
public class WebSocketServer {

    static Log log = LogFactory.getLog(WebSocketServer.class);

    //  静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //  concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //  与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //  接收openId
    private String openId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("openId") String openId) {

        this.session = session;

        //  加入set中
        webSocketSet.add(this);

        //  在线数加1
        addOnlineCount();

        log.info("有新窗口开始监听:"+ openId + ",当前在线人数为" + getOnlineCount());

        this.openId = openId;

        try {
            WebsocketResult result = new WebsocketResult();
            result.setCode(HttpStatus.SUCCESS)
                    .setType(WebSocketConstants.EVENT_CONN)
                    .setMsg("连接成功！");
            sendMessage(JSONObject.toJSONString(result));
        } catch (IOException e) {
            log.error("websocket IO异常");
        }

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {

        //  从set中删除
        webSocketSet.remove(this);

        //  在线数减1
        subOnlineCount();

        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());

    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {

        log.info("收到来自窗口" + openId + "的信息:"+message);

        //  群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     *  实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {

        this.session.getBasicRemote().sendText(message);

    }

    /**
     *  后端推送至前端
     */
    public static void sendInfo(String message,@PathParam("openId") String openId) throws IOException {

        log.info("推送消息到窗口" + openId + "，推送内容:"+message);

        for (WebSocketServer item : webSocketSet) {

            try {

                //  设定只推送给这个openId
               if(item.openId.equals(openId)){
                    item.sendMessage(message);
                }

            } catch (IOException e) {

                continue;

            }

        }

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }


}
