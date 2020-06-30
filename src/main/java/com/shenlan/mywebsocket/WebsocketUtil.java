/**
 * 
 */
package com.shenlan.mywebsocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

/**
 * @author liuxk
 *
 * @version 2020年6月30日 上午10:10:13
 *
 * @description:
 */
public class WebsocketUtil {

	// 静态变量，用来记录当前在线连接数
	private static AtomicLong onlineCount = new AtomicLong(0);

	/**
	 * 记录当前在线的Session
	 */
	private static final Map<String, Session> onlineSessionMap = new ConcurrentHashMap<>();

	/**
	 * 添加session
	 * 
	 * @param userId
	 * @param session
	 */
	public static void addSession(String userId, Session session) {
		WebsocketUtil.addOnlineCount();
		// 此处只允许一个用户的session链接。一个用户的多个连接，我们视为无效。
		onlineSessionMap.putIfAbsent(userId, session);
	}

	/**
	 * 关闭session
	 * 
	 * @param userId
	 */
	public static void removeSession(String userId) {
		WebsocketUtil.subOnlineCount();
		onlineSessionMap.remove(userId);
	}

	/**
	 * 给单个用户推送消息
	 * 
	 * @param session
	 * @param message
	 */
	public static void sendMessage(Session session, String message) {
		if (session == null) {
			return;
		}

		// 同步
		RemoteEndpoint.Async async = session.getAsyncRemote();
		async.sendText(message);
	}

	/**
	 * 向所有在线人发送消息
	 * 
	 * @param message
	 */
	public static void sendMessageForAll(String message) {
		// jdk8 新方法
		onlineSessionMap.forEach((sessionId, session) -> sendMessage(session, message));
	}

	public static synchronized void addOnlineCount() {
		WebsocketUtil.onlineCount.incrementAndGet();
	}

	public static synchronized void subOnlineCount() {
		WebsocketUtil.onlineCount.decrementAndGet();
	}

	public static synchronized int getOnlineCount() {
		return onlineCount.intValue();
	}

}
