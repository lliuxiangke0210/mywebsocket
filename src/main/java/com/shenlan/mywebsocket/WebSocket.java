/**
 * 
 */
package com.shenlan.mywebsocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuxk
 *
 * @version 2020年6月29日 下午6:42:51
 *
 * @description:
 */

@ServerEndpoint(value = "/websocket/v1")
@Component
@Slf4j
public class WebSocket {

	/**
	 * 
	 * @Title: onOpen @Description: 连接建立成功调用的方法 @param: @param session @return:
	 *         void @throws
	 */
	@OnOpen
	public void onOpen(Session session) {
		log.info("连接建立成功调用的方法");
	}

	/**
	 * 
	 * @Title: onClose @Description: 连接关闭调用的方法 @param: @return: void @throws
	 */
	@OnClose
	public void onClose() {
		log.info("连接关闭调用的方法");
	}

	/**
	 * 
	 * @Title: onMessage @Description: 收到客户端消息后调用的方法 @param: @param
	 *         message @param: @param session @param: @throws IOException @return:
	 *         void @throws
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		log.info("收到客户端消息后调用的方法 ");
		session.getBasicRemote().sendText(message);
		log.info(message);
	}

	/**
	 * 
	 * @Title: onError @Description: 发生错误时调用 @param: @param session @param: @param
	 *         error @return: void @throws
	 */
	public void onError(Session session, Throwable error) {
		log.info("发生错误");
		error.printStackTrace();
	}

}
