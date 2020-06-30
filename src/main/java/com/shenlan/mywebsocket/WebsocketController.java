/**
 * 
 */
package com.shenlan.mywebsocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

/**
 * @author liuxk
 *
 * @version 2020年6月30日 上午10:08:40
 *
 * @description: websocket接口处理类
 */

@Component
@ServerEndpoint(value = "/chat/{userid}")
public class WebsocketController {

	/**
	 * 连接事件，加入注解
	 * 
	 * @param userId
	 * @param session
	 */
	@OnOpen
	public void onOpen(@PathParam(value = "userid") String userId, Session session) {

		// 添加到session的映射关系中
		WebsocketUtil.addSession(userId, session);
		String message = "[" + userId + "]加入聊天室！！ 当前在线人数 ：" + WebsocketUtil.getOnlineCount();
		// 广播通知，某用户上线了
		WebsocketUtil.sendMessageForAll(message);
	}

	/**
	 * 连接事件，加入注解 用户断开链接
	 * 
	 * @param userId
	 * @param session
	 */
	@OnClose
	public void onClose(@PathParam(value = "userId") String userId, Session session) {
		String message = "[" + userId + "]退出了聊天室... 当前在线人数 ：" + WebsocketUtil.getOnlineCount();

		// 删除映射关系
		WebsocketUtil.removeSession(userId);
		// 广播通知，用户下线了
		WebsocketUtil.sendMessageForAll(message);
	}

	/**
	 * 当接收到用户上传的消息
	 * 
	 * @param userId
	 * @param session
	 */
	@OnMessage
	public void onMessage(@PathParam(value = "userId") String userId, Session session, String message) {
		String msg = "[" + userId + "]:" + message + "当前在线人数 ：" + WebsocketUtil.getOnlineCount();

		// 直接广播
		WebsocketUtil.sendMessageForAll(msg);
	}

	/**
	 * 处理用户活连接异常
	 * 
	 * @param session
	 * @param throwable
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		try {
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throwable.printStackTrace();
	}
}
