package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.GoodDiv;
import bean.TypeBig;
import bean.TypeSmall;
import service.SearchService;
import service.UserService;
import service.myException.NumberException;
import service.serviceImpl.SearchServiceImpl;
import service.serviceImpl.UserServiceImpl;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	SearchService search=new SearchServiceImpl();
        UserService userService=new UserServiceImpl();
    	String tbid=req.getParameter("bid");
    	String tsid=req.getParameter("sid");
    	String max=req.getParameter("pageMax");
    	int pageMax=6;//Ĭ�����ֵΪ6    ��¼ҳ����ֵ����ֵ  Ҫת����ȥ �� 
         // ��ǰ ҳ��
         int pageNumber=1;
         if(max!=null || !"".equals(max)){
     		try {
     			//�õ���� ҳ��
     			int tmax=Integer.parseInt(max);
     			
     			if(tmax>pageMax){
     				pageMax=tmax;
     			}
     		} catch (NumberFormatException e1) {
     			
     		}
     	}
    	//��ǰҳ
    	String currentPage=req.getParameter("currentpage");
    	//���ж���ҳ
    	if(currentPage==null || "".equals(currentPage)){
    		
    	}else{
    		try {
				pageNumber=Integer.parseInt(currentPage);
				
				if(pageNumber<=0){
					pageNumber=1;
				}
				//  ���Ҫ��ѯ�� ҳ��  ���ڵ�ǰҳ��ʱ��   �ı����ҳ����ֵ
				if(pageMax<pageNumber){
					pageMax=pageNumber;
				}
				
			} catch (NumberFormatException e) {
				//����Ļ� Ĭ�ϸ��� ��һҳ������
				pageNumber=1;
			}
    	}
    	
    	// tmaxΪ���ֵ
    	
    	//��������¼
    	String totalCount=req.getParameter("totalcount");
    	int sid=0;
    	int bid=0;
    	 // ����  �û� �Ѿ��ϴ�����Ʒ������
		   // ��ѯ  ������  �� С����
		List<TypeBig> big=search.findAllTypeBigFlag();
	    List<TypeSmall>small=search.findAllTypeSmallFlag();
	    req.setAttribute("big",big);
	    req.setAttribute("small",small);
		String oper=req.getParameter("oper");
		
		if(oper==null || "".equals(oper)){
			if(tbid==null ||"".equals(tbid)){
	    		
	    		if(tsid==null || "".equals(tsid)){
	    			// ����������Ʒ
	    			GoodDiv gdiv=search.findGoodAll(pageNumber);
	    			//�ж��û��������� tmax  �Ƿ����  ҳ����
	    			
	    			// ������ ��ǰҳ ���� ��ҳ�������
	    			gdiv=changeGoodDiv(gdiv,pageMax,pageNumber);
	    			req.setAttribute("gdiv", gdiv);
	    			req.getRequestDispatcher("course.jsp").forward(req, resp);
	    			return ;
	    		}
	    		try {
					sid=Integer.parseInt(tsid);
				} catch (NumberFormatException e) {
					// ���ֽ����쳣
					resp.sendRedirect("error?oper=err");
					return ;
				}
	    		//����sid ������Ʒ
	    		GoodDiv gdiv=search.findGoodBySid(sid,pageNumber);
	    		gdiv=changeGoodDiv(gdiv,pageMax,pageNumber);
	    		req.setAttribute("gdiv", gdiv);
	    		req.setAttribute("sid", sid);
				req.getRequestDispatcher("course.jsp").forward(req, resp);
	    		return ;
	    		
	    	}else{
	    		try {
					bid=Integer.parseInt(tbid);
				} catch (NumberFormatException e) {
					// ���ֽ����쳣
					resp.sendRedirect("error?oper=err");
					return ;
				}
	    		
	    		if(tsid==null || "".equals(tsid)){
	    			// ����bid��Ʒ
	    			GoodDiv gdiv=search.findGoodByBid(bid,pageNumber);
	    			
	    			gdiv=changeGoodDiv(gdiv,pageMax,pageNumber);
	    			req.setAttribute("gdiv", gdiv);
	    			req.setAttribute("bid", bid);
	    			req.getRequestDispatcher("course.jsp").forward(req, resp);
	    			return ;
	    		}
	    		
	    		try {
					sid=Integer.parseInt(tsid);
				} catch (NumberFormatException e) {
					//���ֽ����쳣
					resp.sendRedirect("error?oper=err");
					return ;
				}
	    		// ���� bid����  sid ��Ʒ
	    		
	    		GoodDiv gdiv=search.findGoodByBidSid(bid, sid,pageNumber);
	    		gdiv=changeGoodDiv(gdiv,pageMax,pageNumber);
	    		
	    		req.setAttribute("gdiv", gdiv);
	    		req.setAttribute("bid", bid);
	    		req.setAttribute("sid", sid);
	    		
				req.getRequestDispatcher("course.jsp").forward(req, resp);
				return ;
	    	}
		}else{
			
			if("search".equals(oper)){
				
				String key=req.getParameter("key");
				
				if(key==null || "".equals(key)){
					//�������е���Ʒ
					GoodDiv gdiv=search.findGoodAll(pageNumber);
					gdiv=changeGoodDiv(gdiv,pageMax,pageNumber);
	    			req.setAttribute("gdiv", gdiv);
	    			req.getRequestDispatcher("search.jsp").forward(req, resp);
					return ;
				}
				key=new String (key.getBytes("ISO-8859-1"),"utf-8").toString();
				GoodDiv gdiv=search.findGoodsByKey(key,pageNumber);
				gdiv=changeGoodDiv(gdiv,pageMax,pageNumber);
				req.setAttribute("gdiv", gdiv);
				req.setAttribute("key", key);
    			req.getRequestDispatcher("search.jsp").forward(req, resp);
				return ;
			}
			
			GoodDiv gdiv=null;
			 if("comprehensive".equals(oper)){
				   //�ղ�����������
				try {
					gdiv = search.findGoodByColAndMesCount22(tbid,tsid,pageNumber);
				} catch (NumberException e) {
					//���� ���ֽ�������
					resp.sendRedirect("error?oper=err");
					return ;
				}
				if(gdiv!=null)
				   gdiv.setType(oper);
				
				    gdiv=changeGoodDiv(gdiv,pageMax,pageNumber);
					req.setAttribute("bid", tbid);
		    		req.setAttribute("sid", tsid);
				   req.setAttribute("gdiv", gdiv);
	   			   req.getRequestDispatcher("course.jsp").forward(req, resp);
				   return ;
				   
			   }else if("fire".equals(oper)){
				   // ���� ��������
				   
				try {
					gdiv = search.findGoodByMesCount1(tbid,tsid,pageNumber);
				} catch (NumberException e) {
					//���� ���ֽ�������
					resp.sendRedirect("error?oper=err");
					return ;
				}
				   if(gdiv!=null)
				   gdiv.setType(oper);
				   
				   gdiv=changeGoodDiv(gdiv,pageMax,pageNumber);
				   req.setAttribute("bid", tbid);
		    		req.setAttribute("sid", tsid);
				   req.setAttribute("gdiv", gdiv);
	   			   req.getRequestDispatcher("course.jsp").forward(req, resp);
				   return ;
			   }else if("newbest".equals(oper)){
				   
				   //���µ�
				   try {
					gdiv=search.findGoodByNewbest(tbid,tsid,pageNumber);
				} catch (NumberException e) {
					//���� ���ֽ�������
					resp.sendRedirect("error?oper=err");
					return ;
				}
				   if(gdiv!=null)
				   gdiv.setType(oper);
				   
				   gdiv=changeGoodDiv(gdiv,pageMax,pageNumber);
				   req.setAttribute("bid", tbid);
		    		req.setAttribute("sid", tsid);
				   req.setAttribute("gdiv", gdiv);
	   			   req.getRequestDispatcher("course.jsp").forward(req, resp);
				   return  ;
			   }
		}
    }

	private GoodDiv changeGoodDiv(GoodDiv gdiv, int pageMax,int pageNumber) {
		
		if(gdiv!=null){
					//���������
				if((pageMax-pageNumber)==6){
					pageMax--;// 3 8  2 7
					gdiv.setPageSize(pageMax);
				}else if((pageMax-pageNumber)>5){
					gdiv=null;
				}else{
					if(gdiv.getTotal()<=6){
						
						gdiv.setPageSize((int) gdiv.getTotal());
					}else{
						gdiv.setPageSize(pageMax);
					}
				}
				
					gdiv.setPageMax(pageMax);
			return gdiv;
			
		}else {
			return null;
		}
	}
}
