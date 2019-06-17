package service.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bean.Collection;
import bean.GidAndCount;
import bean.GoodDiv;
import bean.Goods;
import bean.MesAndColCount;
import bean.Message;
import bean.TypeBig;
import bean.TypeSmall;
import dao.DBHelper;
import service.SearchService;
import service.myException.NumberException;

public class SearchServiceImpl implements SearchService{

	private int pageSize=16;
	
	
	@Override
	public GoodDiv findGoodByBid(int bid,int pageNumber) {
		
		int start=0;
		start=(pageNumber-1)*pageSize;
		
		 List<Goods>t=DBHelper.select
				 ("select g.* from goods g,type t where g.flag=1 and g.gid=t.gid and t.bid=? ",Goods.class,bid);
		 if(t==null ){
			 return null;
		 }
		 GoodDiv gdiv=new GoodDiv();
		 gdiv.setPageNumber(pageNumber);
		 gdiv.setPageSize(pageSize);
		 gdiv.setTotal((t.size()%pageSize==0)?(t.size()/pageSize):(t.size()/pageSize +1));
		 t=DBHelper.select
				 ("select g.* from goods g,type t where g.flag=1 and g.gid=t.gid and t.bid=? limit ?,? ",Goods.class,bid,start,pageSize);
		 for (Goods goods :t) {
	        	goods.setMacc(findMesAndColCount(goods.getGid()+""));
	    	}
		 
		 gdiv.setList(t);
		 gdiv.setType("");
		 gdiv.setBid(bid);
	      return gdiv;
	}

	@Override
	public GoodDiv findGoodAll(int pageNumber) {
		
		 int start=0;
		 start =(pageNumber-1)*pageSize;
		
		 List<Goods>t=DBHelper.select
				 ("select g.* from goods g where  g.flag=1  ",Goods.class);
		 if(t==null ){
			 return null;
		 }
		 GoodDiv gdiv=new GoodDiv();
		
		 gdiv.setPageNumber(pageNumber);
		 gdiv.setPageSize(pageSize);
		 gdiv.setTotal((t.size()%pageSize==0)?(t.size()/pageSize):(t.size()/pageSize +1));
		 gdiv.setPageSize((int)gdiv.getTotal());
		 t=DBHelper.select
				 ("select g.* from goods g where  g.flag=1 limit ?,? ",Goods.class,start,pageSize);
		 for (Goods goods :t) {
	        	goods.setMacc(findMesAndColCount(goods.getGid()+""));
		}
		 
		 gdiv.setList(t);
		 gdiv.setType("");
	      return gdiv;
	}
	@Override
	public List<TypeSmall> findAllTypeSmallFlag() {
		
		List<TypeSmall>list=DBHelper.select("select * from typesmall ", TypeSmall.class);
		if(list==null || list.size()==0){
			return null;
		}
		return list;
	}

	@Override
	public List<TypeBig> findAllTypeBigFlag() {
		
    List<TypeBig>list=DBHelper.select("select * from typebig ", TypeBig.class);
		if(list==null || list.size()==0){
			return null;
		}
		return list;
	}

	@Override
	public GoodDiv findGoodBySid(int sid,int pageNumber) {
		int start=0;
		start=(pageNumber-1)*pageSize;
		 List<Goods>t=DBHelper.select
				 ("select g.* from goods g,type t where g.flag=1 and g.gid=t.gid and (t.sid1=? or t.sid2=? or t.sid3=?)",Goods.class,sid,sid,sid);
		 if(t==null ){
			 return null;
		 }
		 GoodDiv gdiv=new GoodDiv();
		 gdiv.setPageNumber(pageNumber);
		 gdiv.setPageSize(pageSize);
		 gdiv.setTotal((t.size()%pageSize==0)?(t.size()/pageSize):(t.size()/pageSize +1));
		 t=DBHelper.select
				 ("select g.* from goods g,type t where g.flag=1 and g.gid=t.gid and (t.sid1=? or t.sid2=? or t.sid3=?) limit ?,?  ",Goods.class,sid,sid,sid,start,pageSize);
		 for (Goods goods :t) {
	        	goods.setMacc(findMesAndColCount(goods.getGid()+""));
		}
		 gdiv.setList(t);
		 gdiv.setType("");
		 gdiv.setSid(sid);
	     return gdiv;
	}

	@Override
	public GoodDiv findGoodByBidSid(int bid, int sid,int pageNumber) {
		int start=0;
		 start =(pageNumber-1)*pageSize;
		
		 List<Goods>t=DBHelper.select
				 ("select g.* from goods g,type t where g.flag=1 and g.gid=t.gid and t.bid=? and (t.sid1=? or t.sid2=? or t.sid3=?)",Goods.class,bid,sid,sid,sid);
		 if(t==null ){
			 return null;
		 }
		 GoodDiv gdiv=new GoodDiv();
		 gdiv.setPageNumber(pageNumber);
		 gdiv.setPageSize(pageSize);
		 gdiv.setTotal((t.size()%pageSize==0)?(t.size()/pageSize):(t.size()/pageSize +1));
		 t=DBHelper.select
				 ("select g.* from goods g,type t where g.flag=1 and g.gid=t.gid and t.bid=? and (t.sid1=? or t.sid2=? or t.sid3=?) limit ?,?",Goods.class,bid,sid,sid,sid,start,pageSize);
		 for (Goods goods :t) {
	        	goods.setMacc(findMesAndColCount(goods.getGid()+""));
		}
		 gdiv.setList(t);
		 gdiv.setType("");
		 gdiv.setBid(bid);
		 gdiv.setSid(sid);
	      return gdiv;
	}

	@Override
	public GoodDiv findGoodByNewbest(String tbid,String tsid,int pageNumber) throws NumberException {
		int start=0;
		 start =(pageNumber-1)*pageSize;
		int bid=0;
		int sid=0;
		List<Object>params=new ArrayList<>();
		String sql="";
		if(tbid==null || "".equals(tbid)){
			//tbidΪ��
			if(tsid==null || "".equals(tsid)){
				
				//tsidΪ��   ����goods�������ȫ�� �ղ�������Ʒ
				sql="SELECT * from goods where flag=1  ORDER BY gdate DESC";
				
			}else{
				//tsid ��Ϊ��   ����Ϊtsid����     ����goods�������ȫ�� �ղ�������Ʒ
				
				try {
					sid=Integer.parseInt(tsid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT g.* from goods g ,type t where g.flag=1 and g.gid = t.gid and (t.sid1=? or t.sid2=? or t.sid3 =? ) ORDER BY gdate DESC";
				params.add(sid);
				params.add(sid);
				params.add(sid);
			}
		}else{
			//tbid��Ϊ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��  ����Ϊbid    ����goods�������ȫ�� �ղ�������Ʒ
				try {
					bid=Integer.parseInt(tbid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT g.* from goods g ,type t where g.flag=1 and g.gid = t.gid and t.bid=? ORDER BY gdate DESC";
				params.add(bid);
				
				
			}else{
				//tsid ��Ϊ��   ����Ϊbid sid ��  ����goodsn�������ȫ�� �ղ�������Ʒ
			
					try {
						bid=Integer.parseInt(tbid);
						sid=Integer.parseInt(tsid);
					} catch (NumberFormatException e) {
						throw new NumberException("���ֽ�������"+this.getClass().getName());
					}
					sql="SELECT g.* from goods g ,type t where g.flag=1 and g.gid = t.gid and t.bid=? and (t.sid1=? or t.sid2=? or t.sid3 =? ) ORDER BY gdate DESC";
					params.add(bid);
					params.add(sid);
					params.add(sid);
					params.add(sid);
			}
		}
		List<Goods>list=DBHelper.select(sql, Goods.class,params);
		GoodDiv gdiv=new GoodDiv();
		 gdiv.setPageNumber(pageNumber);
		 gdiv.setPageSize(pageSize);
		 gdiv.setTotal((list.size()%pageSize==0)?(list.size()/pageSize):(list.size()/pageSize +1));
		//List<Goods>list1 =DBHelper.select("select * from goods", Goods.class);
		sql=sql+" limit ?,? ";
		params.add(start);
		params.add(pageSize);
		List<Goods>list1=DBHelper.select(sql, Goods.class,params);
		
		int size =0;
		if(list1!=null)
		size =list1.size();
		for (Goods goods : list) {
        	goods.setMacc(findMesAndColCount(goods.getGid()+""));
		}
		if(size>0){
			 int end =start+size ;// 0 1 1 1
			 System.out.println(end +"  "+ start+ "   "+ size);
			 List<Goods>list2=new ArrayList<Goods>();
			 for(int i=start;i<end-1;i++){
					list2.add(list.get(i));
				}
			 gdiv.setList(list2);
		 }
		return gdiv;
	}

	@Override
	public GoodDiv findGoodByMesCount(String tbid,String tsid) throws NumberException {
		int bid=0;
		int sid=0;
		List<Object>params=new ArrayList<>();
		String sql="";
		if(tbid==null || "".equals(tbid)){
			//tbidΪ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��   ����message�������ȫ�� �ղ�������Ʒ
				sql="SELECT m.*  from message m  GROUP BY m.gid ORDER BY COUNT(m.gid) DESC";
			}else{
				//tsid ��Ϊ��   ����Ϊtsid����     ����message�������ȫ�� �ղ�������Ʒ
				
				try {
					bid=Integer.parseInt(tsid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT m.*  from message m ,type t "
		+ "where m.gid=t.gid and (t.sid1=? or t.sid2=? or t.sid3=? ) "
		+ "GROUP BY m.gid ORDER BY COUNT(m.gid) DESC";
				params.add(sid);
				params.add(sid);
				params.add(sid);
			}
		}else{
			//tbid��Ϊ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��  ����Ϊbid    ����message�������ȫ�� �ղ�������Ʒ
				try {
					bid=Integer.parseInt(tbid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT m.*  from message m ,type t where  m.gid=t.gid and t.bid=? GROUP BY m.gid ORDER BY COUNT(m.gid) DESC";
				params.add(tbid);
			}else{
				//tsid ��Ϊ��   ����Ϊbid sid ��  ����message�������ȫ�� �ղ�������Ʒ
			
					try {
						bid=Integer.parseInt(tbid);
						sid=Integer.parseInt(tsid);
					} catch (NumberFormatException e) {
						throw new NumberException("���ֽ�������"+this.getClass().getName());
					}
					sql="SELECT m.*  from message m ,type t "
							+ "where m.gid=t.gid and t.bid=? and (t.sid1=? or t.sid2=? or t.sid3=? ) "
							+ "GROUP BY m.gid ORDER BY COUNT(m.gid) DESC";
				   params.add(bid);
				   params.add(sid);
				   params.add(sid);
				   params.add(sid);
			}
		}
        List<Message>list=DBHelper.select(sql,Message.class,params);
		
		if(list==null || list.size()==0 ){
		      return null;
		}
		GoodDiv gdiv=new GoodDiv();
		for (Message message : list) {
			Goods t=DBHelper.unique("select * from goods where   flag =1 and  gid= ?",Goods.class,message.getGid());
			if(t==null){
				continue;
			}
			 t.setMacc(findMesAndColCount(t.getGid()+""));
			gdiv.getList().add(t);
		}
		return gdiv;
		
	}

	@Override
	public GoodDiv findGoodByColAndMesCount(String tbid,String tsid) throws NumberException  {
		int bid=0;
		int sid=0;
		List<Object>params=new ArrayList<>();
		String sql="";
		if(tbid==null || "".equals(tbid)){
			//tbidΪ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��   ����collection�������ȫ�� �ղ�������Ʒ
				sql="SELECT c.gid , count(c.gid) number  from collection c  GROUP BY gid ORDER BY COUNT(gid) DESC";
			}else{
				//tsid ��Ϊ��   ����Ϊtsid����     ����collection�������ȫ�� �ղ�������Ʒ
				
				try {
					bid=Integer.parseInt(tsid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT c.gid , count(c.gid) number  from collection c ,type t "
		+ "where c.gid=t.gid and (t.sid1=? or t.sid2=? or t.sid3=? ) "
		+ "GROUP BY c.gid ORDER BY COUNT(c.gid) DESC";
				params.add(sid);
				params.add(sid);
				params.add(sid);
			}
		}else{
			//tbid��Ϊ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��  ����Ϊbid    ����collection�������ȫ�� �ղ�������Ʒ
				try {
					bid=Integer.parseInt(tbid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT c.gid , count(c.gid) number  from collection c ,type t where c.gid=t.gid and t.bid=? GROUP BY c.gid ORDER BY COUNT(c.gid) DESC";
				params.add(tbid);
				
				
			}else{
				//tsid ��Ϊ��   ����Ϊbid sid ��  ����collection�������ȫ�� �ղ�������Ʒ
			
					try {
						bid=Integer.parseInt(tbid);
						sid=Integer.parseInt(tsid);
					} catch (NumberFormatException e) {
						throw new NumberException("���ֽ�������"+this.getClass().getName());
					}
					sql="SELECT c.gid , count(c.gid) number  from collection c ,type t "
							+ "where c.gid=t.gid and t.bid=? and (t.sid1=? or t.sid2=? or t.sid3=? ) "
							+ "GROUP BY c.gid ORDER BY COUNT(c.gid) DESC";
				   params.add(bid);
				   params.add(sid);
				   params.add(sid);
				   params.add(sid);
			}
		}
		//���� ���   �ղ�����
		List<GidAndCount> collectionCount=DBHelper.select(sql, GidAndCount.class,params);
		//   select * from collection 
		List<GidAndCount> messageCount=getSqlBestMesCount(tbid,tsid);
		GoodDiv gdiv=new GoodDiv();
		
		List<GidAndCount>list=new ArrayList<>();
		if(collectionCount==null ||collectionCount.size()==0){
			//collectionCount Ϊ��
			
			if(messageCount==null ||messageCount.size()==0){
				//  collectionCount messageCount Ϊ��
				return gdiv;
				
			}else{
				//messageCount ��Ϊ��
				int lenth=messageCount.size();
				for (GidAndCount gidAndCount : messageCount) {
					Goods t=DBHelper.unique("select * from goods where flag=1 and  gid = ?",Goods.class,gidAndCount.getGid());
                    if(t==null){
                    	continue;
                    }
                    t.setMacc(findMesAndColCount(t.getGid()+""));
                    gdiv.getList().add(t);
				}
				return gdiv;
			}
			
		}else{
			//collectionCount ��Ϊ��
			
			if(messageCount==null ||messageCount.size()==0){
				//  messageCount Ϊ��
				int lenth=collectionCount.size();
				for (GidAndCount gidAndCount : collectionCount) {
					Goods t=DBHelper.unique("select * from goods where flag=1 and  gid = ?",Goods.class,gidAndCount.getGid());
                    if(t==null){
                    	continue;
                    }
                    t.setMacc(findMesAndColCount(t.getGid()+""));
                    gdiv.getList().add(t);
				}
				return gdiv;
				
			}else{
				//messageCount ��Ϊ��
				int n=collectionCount.size();
				int m=messageCount.size();
				boolean flag=true;
				// ��messageCount�Ķ���ȫ���ŵ� list��������
				for(int i=0;i<m;i++){
					list.add(messageCount.get(i));
				}
				
				for(int i=0;i<n;i++ ){
					flag=true;
					long cgid=collectionCount.get(i).getGid();
					for(int j=0;j<m;j++){
						long mgid=messageCount.get(j).getGid();
						//������   number ���    ���ǲ���collectionCount.get(i)������
						if(cgid==mgid){
							list.get(j).setNumber(list.get(j).getNumber()+collectionCount.get(j).getNumber());
							
						    flag=false;
							break;
						}
					}
					if(flag){
						list.add(collectionCount.get(i));
					}
				}
				//list ���汣�� gid  ��  number ����Ŀ   ���ݴӴ�С����
				
				//list.sort();
				 Collections.sort(list, new Comparator<GidAndCount>(){
			            /*
			             * int compare(GidAndCount p1, GidAndCount p2) ����һ���������͵����ͣ�
			             * ���ظ�����ʾ��p1 С��p2��
			             * ����0 ��ʾ��p1��p2��ȣ�
			             * ����������ʾ��p1����p2
			             */
			            public int compare(GidAndCount p1,GidAndCount p2) {
			                //����Person�����������������
			                if(p1.getNumber() < p2.getNumber()){
			                    return 1;
			                }
			                if(p1.getNumber() == p2.getNumber()){
			                    return 0;
			                }
			                return -1;
			            }
			        });
				 for (GidAndCount gidAndCount : list) {
					 
					Goods t=DBHelper.unique("select * from goods where flag=1 and  gid=? ",Goods.class,gidAndCount.getGid());
					if(t==null){
						continue;
					}
					
			        t.setMacc(findMesAndColCount(t.getGid()+""));
					
					gdiv.getList().add(t);
				}
				return gdiv;
			}
		}
	}
	
	private List<GidAndCount> getSqlBestMesCount(String tbid,String tsid) throws NumberException{
		int bid=0;
		int sid=0;
		List<Object>params=new ArrayList<>();
		String sql="";
		if(tbid==null || "".equals(tbid)){
			//tbidΪ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��   ����message�������ȫ������Ʒ
				sql="SELECT m.gid ,count(m.gid) number  from message m  GROUP BY gid ORDER BY COUNT(gid) DESC";
			}else{
				//tsid ��Ϊ��   ����Ϊtsid����     ����message�������ȫ�� �ղ�������Ʒ
				
				try {
					bid=Integer.parseInt(tsid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT m.gid ,count(m.gid) number  from message m ,type t "
		+ "where m.gid=t.gid and (t.sid1=? or t.sid2=? or t.sid3=? ) "
		+ "GROUP BY m.gid ORDER BY COUNT(m.gid) DESC";
				params.add(sid);
				params.add(sid);
				params.add(sid);
			}
		}else{
			//tbid��Ϊ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��  ����Ϊbid    ����message�������ȫ�� �ղ�������Ʒ
				try {
					bid=Integer.parseInt(tbid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql=" SELECT m.gid ,count(m.gid) number  from message m ,type t where m.gid=t.gid and t.bid=? GROUP BY m.gid ORDER BY COUNT(m.gid) DESC";
				params.add(tbid);
			}else{
				//tsid ��Ϊ��   ����Ϊbid sid ��  ����message�������ȫ�� �ղ�������Ʒ
			
					try {
						bid=Integer.parseInt(tbid);
						sid=Integer.parseInt(tsid);
					} catch (NumberFormatException e) {
						throw new NumberException("���ֽ�������"+this.getClass().getName());
					}
					sql="SELECT m.gid ,count(m.gid) number from message m ,type t "
							+ "where m.gid=t.gid and t.bid=? and (t.sid1=? or t.sid2=? or t.sid3=? ) "
							+ "GROUP BY m.gid ORDER BY COUNT(m.gid) DESC";
					
				   params.add(bid);
				   params.add(sid);
				   params.add(sid);
				   params.add(sid);
			}
		}
		
		List<GidAndCount> list=DBHelper.select(sql, GidAndCount.class, params);
		return list;
	}

	@Override
	public GoodDiv findGoodsByKey(String key,int pageNumber) {
		int start=0;
		 start =(pageNumber-1)*pageSize;
		String sql="select * from goods where flag=1 and  name like '%"+key+"%'";
		
//		String sql1="select * from type"
		
        List<Goods>list=DBHelper.select(sql, Goods.class);
        GoodDiv gdiv=new GoodDiv(); 
        if(list==null || list.size()==0){
        	return gdiv ;
        }
         gdiv.setPageNumber(pageNumber);
		 gdiv.setPageSize(pageSize);
		 gdiv.setTotal((list.size()%pageSize==0)?(list.size()/pageSize):(list.size()/pageSize +1));
		 sql=sql+" limit ?,? ";
		 list=DBHelper.select(sql, Goods.class,start,pageSize);
		 for (Goods goods : list) {
        	goods.setMacc(findMesAndColCount(goods.getGid()+""));
		}
        
        gdiv.setList(list);
		return gdiv;
	}

	// ��ѯ��Ʒ���ղ��� �� ������
	public MesAndColCount findMesAndColCount(String gid) {
		
MesAndColCount macc=new MesAndColCount();
		
		List<GidAndCount>clist=DBHelper.select("SELECT c.gid, count(c.gid) number from collection c where c.gid=? "
				+ "GROUP BY c.gid ORDER BY COUNT(c.gid) DESC ;", GidAndCount.class,gid);
		if(clist==null || clist.size()==0){
			macc.setCnumber(0);
		}else{
			macc.setCnumber(clist.get(0).getNumber());
		}
		
		List<GidAndCount>mlist=DBHelper.select("SELECT m.gid, count(m.gid) number from message m where m.flag=1 and  m.gid=? "
				+ "GROUP BY m.gid ORDER BY COUNT(m.gid) DESC ;", GidAndCount.class,gid);
		if(mlist==null || mlist.size()==0){
			macc.setMnumber(0);
		}else{
			macc.setMnumber(mlist.get(0).getNumber());
		}
		macc.setSum(macc.getCnumber()+macc.getMnumber());
		return macc;
	}
	
	
	
	public GoodDiv findGoodByColAndMesCount22(String tbid,String tsid,int pageNumber) throws NumberException  {
		int bid=0;
		int sid=0;
		int start=(pageNumber-1)*pageSize;
		List<Object>params=new ArrayList<>();
		String sql="";
		if(tbid==null || "".equals(tbid)){
			//tbidΪ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��   ����collection�������ȫ�� �ղ�������Ʒ
				sql="SELECT *  from goods   ";
			}else{
				//tsid ��Ϊ��   ����Ϊtsid����     ����collection�������ȫ�� �ղ�������Ʒ
				
				try {
					sid=Integer.parseInt(tsid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT g.*  from goods g ,type t "
		+ "where g.flag=1 and g.gid=t.gid and (t.sid1=? or t.sid2=? or t.sid3=? ) ";
				params.add(sid);
				params.add(sid);
				params.add(sid);
			}
		}else{
			//tbid��Ϊ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��  ����Ϊbid    ����collection�������ȫ�� �ղ�������Ʒ
				try {
					bid=Integer.parseInt(tbid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT g.*  from goods g ,type t where g.flag=1 and g.gid=t.gid and t.bid=? ";
				params.add(tbid);
				
				
			}else{
				//tsid ��Ϊ��   ����Ϊbid sid ��  ����collection�������ȫ�� �ղ�������Ʒ
			
					try {
						bid=Integer.parseInt(tbid);
						sid=Integer.parseInt(tsid);
					} catch (NumberFormatException e) {
						throw new NumberException("���ֽ�������"+this.getClass().getName());
					}
					sql="SELECT g.*  from goods g ,type t "
							+ "where g.flag=1 and g.gid=t.gid and t.bid=? and (t.sid1=? or t.sid2=? or t.sid3=? ) ";
				   params.add(bid);
				   params.add(sid);
				   params.add(sid);
				   params.add(sid);
			}
		}
		//���� ���   �ղ�����
		List<Goods> list=DBHelper.select(sql, Goods.class,params);
		 GoodDiv gdiv=new GoodDiv();
         gdiv.setPageNumber(pageNumber);
		 gdiv.setPageSize(pageSize);
		 gdiv.setTotal((list.size()%pageSize==0)?(list.size()/pageSize):(list.size()/pageSize +1));
		 sql=sql+" limit ?,? ";
		 params.add(start);
		 params.add(pageSize);
		 List<Goods> list1=DBHelper.select(sql, Goods.class,params);
		 int size =0;
		  if(list1!=null)
		  size =list1.size();
		  
		for (Goods t : list) {
			 
	        t.setMacc(findMesAndColCount(t.getGid()+""));
		}
				 Collections.sort(list, new Comparator<Goods>(){
			            /*
			             * int compare(GidAndCount p1, GidAndCount p2) ����һ���������͵����ͣ�
			             * ���ظ�����ʾ��p1 С��p2��
			             * ����0 ��ʾ��p1��p2��ȣ�
			             * ����������ʾ��p1����p2
			             */
			            public int compare(Goods p1,Goods p2) {
			                //����Person�����������������
			                if(p1.getMacc().getSum() < p2.getMacc().getSum()){
			                    return 1;
			                }
			                if(p1.getMacc().getSum() == p2.getMacc().getSum()){
			                    return 0;
			                }
			                return -1;
			            }
			        });
				
				 if(size>0){
					 int end =start+size ;
					 List<Goods>list2=new ArrayList<Goods>();
					 for(int i=start;i<end;i++){
							list2.add(list.get(i));
						}
					 gdiv.setList(list2);
				 }
				return gdiv;
	}
	
	
	public GoodDiv findGoodByMesCount1(String tbid,String tsid,int pageNumber) throws NumberException  {
		int bid=0;
		int sid=0;
		int start=(pageNumber-1)*pageSize;
		List<Object>params=new ArrayList<>();
		String sql="";
		if(tbid==null || "".equals(tbid)){
			//tbidΪ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��   ����collection�������ȫ�� �ղ�������Ʒ
				sql="SELECT *  from goods where flag =1";
			}else{
				//tsid ��Ϊ��   ����Ϊtsid����     ����collection�������ȫ�� �ղ�������Ʒ
				
				try {
					sid=Integer.parseInt(tsid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT g.*  from goods g ,type t "
		+ "where g.flag=1 and g.gid=t.gid and (t.sid1=? or t.sid2=? or t.sid3=? ) ";
				params.add(sid);
				params.add(sid);
				params.add(sid);
			}
		}else{
			//tbid��Ϊ��
			if(tsid==null || "".equals(tsid)){
				//tsidΪ��  ����Ϊbid    ����collection�������ȫ�� �ղ�������Ʒ
				try {
					bid=Integer.parseInt(tbid);
				} catch (NumberFormatException e) {
					throw new NumberException("���ֽ�������"+this.getClass().getName());
				}
				sql="SELECT g.*  from goods g ,type t where g.flag=1 and g.gid=t.gid and t.bid=? ";
				params.add(tbid);
				
			}else{
				//tsid ��Ϊ��   ����Ϊbid sid ��  ����collection�������ȫ�� �ղ�������Ʒ
			
					try {
						bid=Integer.parseInt(tbid);
						sid=Integer.parseInt(tsid);
					} catch (NumberFormatException e) {
						throw new NumberException("���ֽ�������"+this.getClass().getName());
					}
					sql="SELECT g.*  from goods g ,type t "
							+ "where g.flag=1 and g.gid=t.gid and t.bid=? and (t.sid1=? or t.sid2=? or t.sid3=? ) ";
				   params.add(bid);
				   params.add(sid);
				   params.add(sid);
				   params.add(sid);
			}
		}
		//���� ��� ������������Ʒ
		List<Goods> list=DBHelper.select(sql, Goods.class,params);
		GoodDiv gdiv=new GoodDiv();
        gdiv.setPageNumber(pageNumber);
		 gdiv.setPageSize(pageSize);
		 gdiv.setTotal((list.size()%pageSize==0)?(list.size()/pageSize):(list.size()/pageSize +1));
		 sql=sql+" limit ?,? ";
		 params.add(start);
		 params.add(pageSize);
		 List<Goods>list1=DBHelper.select(sql, Goods.class,params);
		 int size =0;
		 if(list1!=null)
		 size=list1.size();
		
		for (Goods t: list) {
	        t.setMacc(findMesAndColCount(t.getGid()+""));
	        t.getMacc().setSum(t.getMacc().getCnumber()+t.getMacc().getMnumber());
		}
				 Collections.sort(list, new Comparator<Goods>(){
			            /*
			             * int compare(GidAndCount p1, GidAndCount p2) ����һ���������͵����ͣ�
			             * ���ظ�����ʾ��p1 С��p2��
			             * ����0 ��ʾ��p1��p2��ȣ�
			             * ����������ʾ��p1����p2
			             */
			            public int compare(Goods p1,Goods p2) {
			                //����Person�����������������
			                if(p1.getMacc().getMnumber() < p2.getMacc().getMnumber()){
			                    return 1;
			                }
			                if(p1.getMacc().getMnumber() == p2.getMacc().getMnumber()){
			                    return 0;
			                }
			                return -1;
			            }
			        });
				 
				 if(size>0){
					 int end =start+size ;
					 List<Goods>list2=new ArrayList<Goods>();
					 for(int i=start;i<end;i++){
							list2.add(list.get(i));
						}
					 gdiv.setList(list2);
				 }
				return gdiv;
	}
	
	
	
	
}
