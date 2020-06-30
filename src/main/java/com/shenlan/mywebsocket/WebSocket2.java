/**
 * 
 */
package com.shenlan.mywebsocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuxk
 *
 * @version 2020年6月29日 下午6:42:51
 *
 * @description:
 */

@ServerEndpoint(value = "/websocket/v2/{userId}")
@Component
@Slf4j
public class WebSocket2 {

	// 静态变量，用来记录当前在线连接数
	private static AtomicLong onlineCount = new AtomicLong(0);
	// concurrent包的线程安全Set，用来存放客户端对象
	private static final ConcurrentHashMap<String, WebSocket2> clients = new ConcurrentHashMap<>();
	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;
	// 用户唯一标识符
	private String userId = "";

	@OnOpen
	public void onOpen(Session session, @PathParam("userId") String userId) {
		this.session = session;
		this.userId = userId;
		log.info("有新的客户端连接进来了，客户端id是： " + session.getId());
		if (clients.containsKey(userId)) {
			clients.remove(userId);
			clients.put(userId, this);
		} else {
			clients.put(userId, this);
			WebSocket2.addOnlineCount();
		}

		log.info("用户：" + userId + ", 当前在线人数为:" + getOnlineCount());

		try {
			sendMessage(userId, "连接成功");
		} catch (Exception e) {
			log.error("用户:" + userId + ",网络异常!");
		}

	}

	@OnClose
	public void onClose() {
		if (clients.containsKey(userId)) {
			clients.remove(userId);
			subOnlineCount();
		}
		log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("用户" + userId + "发来消息， 报文：" + message);
		try {
			session.getBasicRemote().sendText("来自服务的的反馈消息 ，成功接收到你的消息 ：" + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
		error.printStackTrace();
	}

	/**
	 * 发送消息到指定客户端
	 * 
	 * @param id
	 * @param message
	 */
	public void sendMessage(String userId, String message) throws Exception {
		// 根据id,从map中获取存储的webSocket对象
		WebSocket2 webSocket = clients.get(userId);
		if (!ObjectUtils.isEmpty(webSocket)) {
			// 当客户端是Open状态时，才能发送消息
			if (webSocket.session.isOpen()) {
				webSocket.session.getBasicRemote().sendText(message);
			} else {
				log.error("websocket session={} is closed ", userId);
			}
		} else {
			log.error("websocket session={} is not exit ", userId);
		}
	}

	/**
	 * 发送消息到所有客户端
	 * 
	 */
	public void sendAllMessage(String msg) throws Exception {
		log.info("online client count={}", clients.size());
		Set<Map.Entry<String, WebSocket2>> entries = clients.entrySet();
		for (Map.Entry<String, WebSocket2> entry : entries) {
			String cid = entry.getKey();
			WebSocket2 webSocket = entry.getValue();
			boolean sessionOpen = webSocket.session.isOpen();
			if (sessionOpen) {
				webSocket.session.getBasicRemote().sendText(msg);
			} else {
				log.info("cid={} is closed,ignore send text", cid);
			}
		}
	}

	public static void addOnlineCount() {
		onlineCount.incrementAndGet();
	}

	public static void subOnlineCount() {
		onlineCount.decrementAndGet();
	}

	public static int getOnlineCount() {
		return onlineCount.intValue();
	}

}
