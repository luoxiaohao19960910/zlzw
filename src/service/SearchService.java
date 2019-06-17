package service;

import java.util.List;

import bean.GoodDiv;
import bean.MesAndColCount;
import bean.TypeBig;
import bean.TypeSmall;
import service.myException.NumberException;

public interface SearchService {

	//���ݴ����������� ��Ʒ��Ϣ
	GoodDiv findGoodByBid(int bid,int pageNumber);
	List<TypeSmall> findAllTypeSmallFlag();
	List<TypeBig> findAllTypeBigFlag();
	
	//����key��������Ʒ
	GoodDiv findGoodsByKey(String key,int pageNumber);
	    //��ѯȫ��
		GoodDiv findGoodAll(int pageNumber);
		//����С��������ѯ
		GoodDiv findGoodBySid(int sid,int pageNumber);
		//���ݴ����������С������������Ʒ
		GoodDiv findGoodByBidSid(int bid,int sid,int pageNumber);
		//��������ʱ����������Ʒ
		GoodDiv findGoodByNewbest(String tbid,String tsid,int pageNumber) throws NumberException;
		//��������������������Ʒ
		GoodDiv findGoodByMesCount(String tbid,String tsid) throws NumberException;
		GoodDiv findGoodByMesCount1(String tbid,String tsid,int pageNumber) throws NumberException;
		//�����ղ�������������Ʒ
		 GoodDiv findGoodByColAndMesCount22(String tbid,String tsid,int pageNumber) throws NumberException ; 
		
		GoodDiv findGoodByColAndMesCount(String tbid,String tsid) throws NumberException ;
}
