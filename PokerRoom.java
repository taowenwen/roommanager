package com.poker;

import java.io.IOException;
import java.util.Collection;
/**
 * 管理玩家的房间类，提供与房间处理有关的接口
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.springframework.web.socket.WebSocketSession;
import com.alex.chatroom.controller.StateManager;
import bridge.domain.PlayerPosition;


public class PokerRoom {
	private Set<User> players=new HashSet<User>();//保存房间内所有玩家的session的变量
	private StateManager manager;//与房间绑定的游戏状态处理机
	private Map<String, PlayerPosition> positionMap=new HashMap<>();//处理玩家在牌桌中的对应位置
	private String hostId;//保存房主id的方法
	
	public PokerRoom(User user) {//房主创建房间的构造方法
		this.hostId=user.getUserId();
		players.add(user);
		positionMap.put(user.getUserId(), PlayerPosition.NORTH);
		manager=new StateManager(this);
	}
	
	public boolean add(User user) {//添加玩家的方法
		if(players.size()>4||players.contains(user)||user.getRoom()!=null) return false;
		players.add(user);
		
		Iterator it = players.iterator();
		while (it.hasNext()) {
			System.out.println("成员");
			System.out.println(it.next());
		}
		
		if(positionMap.containsValue(PlayerPosition.NORTH)==false) {//对房间空位进行判断，按照玩家加入房间的顺序依次判断北东南西空位，有空位则添加
        	positionMap.put(user.getUserId(), PlayerPosition.NORTH);
        }else if(positionMap.containsValue(PlayerPosition.EAST)==false) {
        	positionMap.put(user.getUserId(), PlayerPosition.EAST);
        }else if(positionMap.containsValue(PlayerPosition.SOUTH)==false) {
        	positionMap.put(user.getUserId(), PlayerPosition.SOUTH);
        }else if(positionMap.containsValue(PlayerPosition.WEST)==false) {
        	positionMap.put(user.getUserId(), PlayerPosition.WEST);
        }
		user.setRoom(this);

		
		Set<String> keySet = positionMap.keySet();
		Iterator<String> it1 = keySet.iterator();
		while(it1.hasNext()){
			String ID = it1.next();
			PlayerPosition stu = positionMap.get(ID);
			String fullname=stu.getFullName();
			System.out.println("加入");
			System.out.println(ID+""+fullname);
		}
		return true;
	}
	
	public boolean exit(User user) {//玩家退出的方法
		if(!players.contains(user)&&user.getRoom()==null) return false;
		if(user.getUserId()==hostId) {
			RoomManager room=new RoomManager ();
			boolean result = room.updateRoom(hostId);
			players.remove(user);
			positionMap.remove(user.getUserId());
			user.exitRoom(this);
			return result;
		}
		else{
		players.remove(user);
		positionMap.remove(user.getUserId());
		user.exitRoom(this);
		}
		
		return true;
	}
	
	public PlayerPosition findPosition(String userId) {
		/**
		 * 查找用户位置的方法
		 */
		return positionMap.get(userId);
	}
	
	public Set<User> getPlayers(){
		return players;
	}
	
	public String getPlayersid(){//获取除房主外的玩家id
		String ID = null;
		Set<String> keySet = positionMap.keySet();
		Iterator<String> it1 = keySet.iterator();
		while(it1.hasNext()){
			ID = it1.next();
			if(ID!=hostId) break;
		}
		return ID;
	}
	
	public void updateroom(String hostId) {//转让房间后更新房主信息
		this.hostId=hostId;
	}
	public int size() {
		/**
		 * 获取房间人数的方法
		 */
		return players.size();
	} 
	
	public String getHostId(){
		/**
		 * 获取房主id的方法
		 */
		return hostId;
	}
	
    public void handelMessage(WebSocketSession session,String message) {//处理与session绑定的message的方法
		manager.handleMessage(matchUser(session),message);
	}
	
	public void sendToAll(Object object) {
		for (User user : players) {
			try {
				user.receive(object);
			} catch (IOException e) {
				// TODO 添加断线处理方法
				e.printStackTrace();
			}
		}
	}
	public User matchUser(WebSocketSession session) {
		/**
		 * 根据session匹配房间内玩家的方法，由于房间内只有4个人，故费时较少
		 */
		for (User user : players) {
			if(user.getSession().getId()==session.getId()) {
				return user;
			}
		}
		return null;
	}
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebSocketSession session=null;
		User user1=new User("aaa",session);
		User user2=new User("bbb",session);
		User user3=new User("ccc",session);
		User user4=new User("ddd",session);
			
		RoomManager demo=new RoomManager ();
		boolean re1=demo.creatRoom(user1, "1");
		PokerRoom room=demo.findRoom("1");
		room.add(user2);
		room.add(user3);
		room.exit(user2);
		room.add(user4);
		room.exit(user1);
		room.exit(user3);
		room.exit(user4);
		

	}*/
	
	
}
