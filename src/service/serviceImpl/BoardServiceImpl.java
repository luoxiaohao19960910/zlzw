package service.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bean.GidAndCount;
import bean.Goods;
import bean.Information;
import bean.Message;
import bean.MessageAndUser;
import bean.User;
import dao.DBHelper;
import service.BoardService;

/**
 * ��ҳ����ʾ������ص� ��Ϣ��
 * 
 * һ�� ��Ʒ������     ��Ʒ�� ��һ���ϴ���ͼƬ
 *  ������������������Ʒ��������   
 * 
 *  ���˻ظ����   ����ظ����˵�    
 * @author Administrator
 *
 */
public class BoardServiceImpl implements BoardService{

	@Override
	public String rely(String uid1, String uid2, String text,String tmid,String gid) {
		/**
		 * �û����Թ���
		 */
		String flag="";
		Timestamp time=new Timestamp(System.currentTimeMillis());
		List<List<Object>>list=DBHelper.insert("insert into message(gid,uid1,uid2,tmid,msg,mdate) value(?,?,?,?,?,?)",gid,uid1,uid2,tmid,text,time);
		if(list==null || list.size()==0){
			//����ʧ��
			return "null";
		}
		
		return list.get(0).get(0).toString();
	}

	@Override
	public String board(String uid1, String text, String gid) {
		String flag="";
		Timestamp time=new Timestamp(System.currentTimeMillis());
		List<List<Object>>list=DBHelper.insert("insert into message(gid,uid1,msg,mdate) value(?,?,?,?)",gid,uid1,text,time);
		if(list==null || list.size()==0){
			//����ʧ��
			return "null";
		}
		
		return list.get(0).get(0).toString();
	}

	@Override
	public List<Information> information(String uid) {
		/**
		 * 
		 * ��һ�����  ����������Ʒ��������     
		 * 
		 * �ڶ������ ���ڱ�����Ʒ��������  ������˻ظ���
		 * 
		 * 
		 * 
		 * һ����Ϣ   һ�� gdiv   �������  �û���Ϣ ����Ʒ��Ϣ  ���������� ��Ϣ
		 * ���������Ϣ��ͬʱ
		 * 
		 * һ��gdiv ����һ������Ʒ��Ϣ���û� ����
		 * 
		 * չʾ�����Ʒ�� ���лظ������Ϣ ���� ʱ��Ĵ�С����
		 * 
		 * @param uid
		 */
		//��һ�����  ����������Ʒ��������     
		
		// ���� ÿ�� div��������Ϣ
		List<Information>boardList=new ArrayList<Information>();
		
		//��ѯ���Լ� ���������� ��Ʒ  ���˸��Լ������� 
		List<Message>list=DBHelper.select("select m.* from goods g  ,message  m "
				+ "where m.flag=1 and  g.uid=? and g.gid=m.gid and m.uid1!=? and m.uid2=0 ", Message.class, uid,uid);
		User my=DBHelper.unique("select * from user where uid =?", User.class, uid);
		
		if(list!=null && list.size()>0){
			
			for (Message message : list) {
				
				//�Լ���������Ʒ��  ��Ϣ
				Goods good=DBHelper.unique("select * from goods where  gid =?", Goods.class, message.getGid());
				if(good==null){
					continue;
				}
				
				//�õ�   �ظ�������Ϣ �˵���Ϣ
				User user1=DBHelper.unique("select * from user where uid= ?", User.class,message.getUid1());
			    if(user1 ==null ){
			    	continue;
			    }
			    message.setTmid(message.getMid());
			    Information ift=new Information();
			    ift.setIndex(message.getMid());
				ift.setGood(good);
				ift.setMessage(message);
				ift.setUser(my);
				ift.setUser1(user1);
				boardList.add(ift);
				int resule=DBHelper.update("update message set isread =0 where mid= ?", message.getMid());
				/*if(resule>0){
					//�����ɹ�
					boardList.add(ift);
				}*/
			}
		}
		//�ڶ������ ���ڱ�����Ʒ��������  ������˻ظ���
		List<Message>list2= DBHelper.select("select * from message where flag = 1 and  uid2=? ORDER BY mid DESC", 
				Message.class,uid);
		
		
		if(list2!=null && list2.size()>0){
			
			//�����ظ������  ��������Ϣ  ȥ gid  ��uid   �������ǵ���Ϣ  ��    Information
			for (Message message : list2) {
				
				Goods good=DBHelper.unique("select * from goods where  gid =?", Goods.class, message.getGid());
				if(good==null){
					continue;
				}
				
				//�õ�  ��Ʒ�ķ����˵���Ϣ
				User user=DBHelper.unique("select * from user where uid=? ", User.class,good.getUid());
				
				if(user==null){
					continue;
				}
				
				//�õ�   �ظ�������Ϣ �˵���Ϣ
				User user1=DBHelper.unique("select * from user where uid= ?", User.class,message.getUid1());
			    if(user1 ==null ){
			    	continue;
			    }
			    
			    Information ift=new Information();
			    
			    ift.setGood(good);
			    
			    ift.setIndex(message.getMid());
			    ift.setMessage(message);
			    ift.setUser(user);
			    ift.setUser1(user1);
			    int resule=DBHelper.update("update message set isread =0 where mid= ?", message.getMid());
			    
			    boardList.add(ift);
			    
			    /*if(resule>0){
					//�����ɹ�
					boardList.add(ift);
				}*/
			}
		}
		
		// boardList  ����  index  ��������
		Collections.sort(boardList, new Comparator<Information>(){
            /*
             * int compare(GidAndCount p1, GidAndCount p2) ����һ���������͵����ͣ�
             * ���ظ�����ʾ��p1 С��p2��
             * ����0 ��ʾ��p1��p2��ȣ�
             * ����������ʾ��p1����p2
             */
            public int compare(Information p1,Information p2) {
                //����Person�����������������
                if(p1.getIndex() < p2.getIndex()){
                    return 1;
                }
                if(p1.getIndex() == p2.getIndex()){
                    return 0;
                }
                return -1;
            }
        });
		return boardList;
	}

	@Override
	public String delBoard(String mid) {
		int result =DBHelper.update("update message set flag =0 where mid =?  ", mid);
		int result1=DBHelper.update("update message set flag =0 where tmid =?  ", mid);
		result+=result1;
		if(result>0){
		    return result+"";
		}else{
			return "0";
		}
	}

	@Override
	public String delReply(String mid) {
		int result =DBHelper.update("update message set flag =0 where mid =? ",mid);
		return result+"";
	}

}
