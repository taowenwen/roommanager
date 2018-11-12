package com.poker;
/**
 * 用于管理各个房间的方法，属性均为静态属性，方法均为静态方法
 *
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.alex.chatroom.controller.StateManager;
import bridge.domain.*;

public class RoomManager {
	private static final Map<String, PokerRoom> roomMap=new HashMap<>();//保存房间号对应的房间
	private static final Map<String, PokerRoom> userMap=new HashMap<>();//保存用户id对应的房间
	public static boolean creatRoom(User user,String roomId) {
		/**
		 * 创建房间的方法，若创建成功返回true，否则返回false
		 */
		if(userMap.containsKey(user.getUserId())||roomMap.containsKey(roomId)) return false;//用户已在房间或房间已存在时返回false
		PokerRoom room=new PokerRoom(user);
		roomMap.put(roomId, room);//添加相应绑定
		userMap.put(user.getUserId(), room);

		Set<String> keySet = userMap.keySet();
		Iterator<String> it1 = keySet.iterator();
		while(it1.hasNext()){
			String ID = it1.next();
			PokerRoom stu = userMap.get(ID);
			System.out.println(ID+" "+stu.toString());
		}
		return true;
	}
	
	public static PokerRoom findRoom(String roomName) {//根据房间名获取房间的方法
		return roomMap.get(roomName);
	}
	public static PokerRoom hostfindRoom(String hostId) {//根据房主获取房间的方法
		return userMap.get(hostId);
	}
	
	public static boolean updateRoom(String hostId) {
		/**
		 * 房主退出房间的方法，若退出成功返回true，否则返回false
		 * 若房间还有其他人，则转让房间房主
		 */
		String roomId=null;
		PokerRoom room=userMap.get(hostId);
		Set<String> keySet = roomMap.keySet();
		Iterator<String> it1 = keySet.iterator();
		while(it1.hasNext()){
			String ID = it1.next();
			PokerRoom stu = roomMap.get(ID);
			if(room==stu) {
				roomId=ID;
				break;
			}
		}
		if(!userMap.containsKey(hostId)||!roomMap.containsKey(roomId)) {
			System.out.println("用户已不在房间或房间不存在!");
			return false;
			}//用户已不在房间或房间不存在时返回false
		
		if(room.size()>1) {//房间内还有其他人时转让房间
			System.out.println("房间转让");
			String id = room.getPlayersid();
			if(id!=null) {
				room.updateroom(id);
				userMap.remove(hostId);
				userMap.put(id, room);
			}
		}
		else { //房间内无其他人，解散房间
		System.out.println("房间解散");
		roomMap.remove(roomId);//房主退出后房间删除
		userMap.remove(hostId);
		}
		
		Set<String> keySet1 = userMap.keySet();
		Iterator<String> it2 = keySet1.iterator();
		while(it2.hasNext()){
			String ID1 = it2.next();
			PokerRoom stu1 = userMap.get(ID1);
			System.out.println(ID1+" "+stu1.toString());
		}
		return true;
	}
	
	
}
