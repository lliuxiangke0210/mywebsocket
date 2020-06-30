/**
 * 
 */
package com.shenlan.mywebsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuxk
 *
 * @version 2020年6月30日 上午11:16:45
 *
 * @description:
 */
@RestController
@RequestMapping("/api/v2")
public class MyController {

	/**
	 * 注入webSocket2
	 **/
	@Autowired
	private WebSocket2 webSocket2;

	/**
	 * 向指定客户端发消息
	 * 
	 * @param id
	 * @param msg
	 */
	@PostMapping(value = "sendMsgToClientById")
	public void sendMsgToClientById(@RequestParam String id, @RequestParam String text) {
		try {
			webSocket2.sendMessage(id, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发消息到所有客户端
	 * 
	 * @param msg
	 */
	@PostMapping(value = "sendMsgToAllClient")
	public void sendMsgToAllClient(@RequestParam String text) {
		try {
			webSocket2.sendAllMessage(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
