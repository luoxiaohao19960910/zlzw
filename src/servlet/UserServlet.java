package servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import bean.DynamicDiv;
import bean.GoodDiv;
import bean.Goods;
import bean.PageBean;
import bean.TypeBig;
import bean.TypeSmall;
import bean.User;
import bean.UserInfo;
import dao.DBHelper;
import service.GoodsService;
import service.UserService;
import service.serviceImpl.GoodsServiceImpl;
import service.serviceImpl.UserServiceImpl;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/user.s")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserService userService = new UserServiceImpl();
	private GoodsService goodsService = new GoodsServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String oper = req.getParameter("oper");
		if (oper == null || "".equals(oper)) {
			resp.sendRedirect("login.jsp");
			return;
		}
		if ("login".equals(oper)) {
			login(req, resp);
		} else if ("register".equals(oper)) {
			req.getRequestDispatcher("register.jsp").forward(req, resp);
			return;
		} else if ("index".equals(oper)) {
			index(req, resp);
		} else if ("mySrc".equals(oper)) {
			mySrc(req, resp);
		} else if ("uploadfile".equals(oper)) {
			uploadfile(req, resp);
		} else if ("myDownload".equals(oper)) {
			myDownload(req, resp);
		} else if ("favorite".equals(oper)) {
			favorite(req, resp);
		} else if ("vip".equals(oper)) {
			vip(req, resp);
		}else if ("info".equals(oper)) {
			info(req, resp);
		} else if ("loginservice".equals(oper)) {
			loginservice(req, resp);
		} else if ("regservice".equals(oper)) {
			register(req, resp);
			return ;
		}else if("quit".equals(oper)){
			clearUserCookie(req,resp);
			return ;
		}else if("reg".equals(oper)){
			req.getRequestDispatcher("register").forward(req, resp);
			return ;
		}else if("findpwd".equals(oper)){
			req.getRequestDispatcher("findpwd.jsp").forward(req, resp);
			return ;
		}else if("search".equals(oper)){
			req.getRequestDispatcher("search.jsp").forward(req, resp);
			return ;
		} else if ("quit".equals(oper)) {
			clearUserCookie(req, resp);
			return;
		} else if ("editInfo".equals(oper)) {
			editInfo(req, resp);
			return;
		} else if ("updateInfo".equals(oper)) {
			updateInfo(req, resp);
			return;
		} else if ("updateHead".equals(oper)) {
			updateHead(req, resp);
			return;
		} else if ("isFollow".equals(oper)) {
			isFollow(req, resp);
			return;
		} else if ("follow".equals(oper)) {
			follow(req, resp);
			return;
		} else if ("queryFollow".equals(oper)) {
			queryFollow(req, resp);
			return;
		} else if ("queryFans".equals(oper)) {
			queryFans(req, resp);
			return;
		}else if ("queryFriend".equals(oper)) {
			queryFriend(req, resp);
			return;
		} else if ("cancelFollow".equals(oper)) {
			cancelFollow(req, resp);
			return;
		} else if ("delFriend".equals(oper)) {
			delFriend(req, resp);
			return;
		} else if ("queryfff".equals(oper)) {
			queryfff(req, resp);
			return;
		}else if("query".equals(oper)){
			query(req,resp);
			return ;
		}else {
			resp.sendRedirect("user.s?oper=index");
			return;
		}
	}
	
	
	/**
	 * ��ѯ ��Ϣ��Ŀ
	 * @param req
	 * @param resp
	 */
	private void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid =req.getParameter("uid");
		
		if(uid==null || "".equals(uid)){
			return  ;
		}
		int number=userService.query(uid);
		resp.getWriter().write(number+"");
		return ;
	}



	/**
	 * ��ѯ���ѣ���ע����˿��
	 * ck
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void queryfff(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			return;
		}
		// �����û��Ĺ�ע��(follow)����˿��(fans)��������(friend)  
		int followNum = userService.countFollow(uid);
		int fansNum = userService.countFans(uid);
		int friendNum = userService.countFriend(uid);
		resp.getWriter().append(followNum+";"+fansNum+";"+friendNum);
	}

	/**
	 * ɾ������
	 * @param req
	 * @param resp
	 */
	private void delFriend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		String infouid = req.getParameter("Infouid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		int result = userService.delFriend(uid,infouid);
		if(result > 0){
			resp.getWriter().append("�����ɹ�");
		}else{
			resp.getWriter().append("����ʧ��");
		}
	}
	/**
	 * ȡ����ע
	 * @param req
	 * @param resp
	 */
	private void cancelFollow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		String infouid = req.getParameter("Infouid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		int result = userService.cancelFollow(uid,infouid);
		if(result > 0){
			resp.getWriter().append("�����ɹ�");
		}else{
			resp.getWriter().append("����ʧ��");
		}
	}
	/**
	 * ��ѯ�����û�
	 * @param req
	 * @param resp
	 */
	private void queryFriend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		List<User> userList = userService.queryFriend(uid);		
		String userString = JSON.toJSONString(userList);
		resp.getWriter().append(userString);
		
	}
	/**
	 * ��ѯ��˿�û�
	 * @param req
	 * @param resp
	 */
	private void queryFans(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		List<User> userList = userService.queryFans(uid);		
		String userString = JSON.toJSONString(userList);
		resp.getWriter().append(userString);
	}
	/**
	 * ��ѯ��ע���û�
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void queryFollow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		List<User> userList = userService.queryFollow(uid);		
		String userString = JSON.toJSONString(userList);
		resp.getWriter().append(userString);
	}

	/**
	 * ��ע
	 * @param req
	 * @param resp
	 */
	private void follow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		String infouid = req.getParameter("Infouid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		long result = userService.follow(uid, infouid);
		if(result > 0){
			resp.getWriter().append("��ע�ɹ�");
		}else{
			resp.getWriter().append("��������æ,���Ժ�����!!!");
		}
	}
	/**
	 * �жϸ��û��Ƿ񱻹�ע
	 * ck
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void isFollow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		String infouid = req.getParameter("Infouid");
		String ifs = userService.isFollow(uid,infouid);
		resp.getWriter().append(ifs);
	}

	/**
	 * �����û�ͷ��
	 * @param req
	 * @param resp
	 */
	private void updateHead(HttpServletRequest req, HttpServletResponse resp) {
		String uid = req.getParameter("uid");
		
		// ���������ļ��Ĺ�����
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// �����ļ���������
		ServletFileUpload sfu = new ServletFileUpload(factory);
		// ���õ����ļ������ֵ�����ֽ�Ϊ��λ
		sfu.setFileSizeMax(1024*1024*2);
		List<FileItem> fList;
		try {
			fList = sfu.parseRequest(req);
			FileItem fi = fList.get(0);
			// ��ȡԭʼ�ļ���
			String name = fi.getName();
			// ����ı��������
			String fieldName = fi.getFieldName();
			// �Ƿ�����ͨ�ı�
			boolean formField = fi.isFormField();
			if (!formField) {
				if (fieldName != null && !"".equals(fieldName)) {
					// ����UUID�ļ�����ʹ�ļ��������ظ�
					String uuid = UUID.randomUUID().toString();
					uuid = uuid.replaceAll("-", "");
					// ���ԭʼ�ļ����ĺ�׺��
					String suffix = name.substring(name.lastIndexOf("."));
					// ָ��Ҫ�ϴ���Ŀ¼
					String uploadPath = "D:/upload";
					File files = new File(uploadPath);
					if(!files.exists()){
						files.mkdirs();
					}
					// �����ļ�����
					File file = new File(uploadPath, uuid + suffix);
					InputStream is = fi.getInputStream();
					//д�����
					InputStream bis = new BufferedInputStream(is);
					OutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
					byte[] bs = new byte[1024];
					int length = 0;
					while((length = bis.read(bs, 0, bs.length)) != -1){
						bos.write(bs, 0, bs.length);
						bos.flush();//��ջ���������ʹ������������ȫ��д��
					}
					bis.close();
					bos.close();
					int result = userService.updateHead(uid,"http://localhost/" + uuid + suffix);
					if(result > 0){
						resp.getWriter().append("ͷ���ϴ��ɹ�");
						User u=(User) req.getSession().getAttribute("user");
						u.setHead("http://localhost/" + uuid + suffix);
					}else{
						resp.getWriter().append("����æ�����Ժ�����");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if( e instanceof FileSizeLimitExceededException){
					resp.getWriter().append("�ϴ�ʧ�ܣ�ͼƬ����");
				}else{
					resp.getWriter().append("����æ�����Ժ�����");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * �����û���Ϣ
	 * 
	 * @param req
	 * @param resp
	 */
	private void updateInfo(HttpServletRequest req, HttpServletResponse resp) {
		/**
		 *   �û��޸ĸ�����Ϣ
		 */
		String uid = req.getParameter("uid");
		String json = req.getParameter("json");
		UserInfo userInfo = new Gson().fromJson(json, UserInfo.class);
		String hobby = "";
		for (int i = 0; i < userInfo.getHobby().length; i++) {
			hobby += userInfo.getHobby()[i] + "+";
		}
		hobby = hobby.substring(0, hobby.lastIndexOf("+"));
		int result = userService.updateInfo(userInfo, hobby, uid);
		try {
			if (result > 0) {
				resp.getWriter().append("�޸ĳɹ�");
				User u=(User) req.getSession().getAttribute("user");
				u.setUname(userInfo.getUname());
				u.setSex(userInfo.getSex());
				u.setTel(userInfo.getTel());
				u.setEmail(userInfo.getEmail());
				u.setAddr(userInfo.getAddr());
				u.setSign(userInfo.getSign());
				u.setBirthday(userInfo.getBirthday());
			} else {
				resp.getWriter().append("�޸�ʧ��");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void editInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 *  �û��޸� ������Ϣҳ������ݴ��
		 */
		String uid = req.getParameter("uid");
		// �����û���Ϣ
		User user = userService.findUserById(uid);
		List<String> hobbyList = new ArrayList<String>();
		String[] hobbys = user.getHobby().split("[+]");
		for (int i = 0; i < hobbys.length; i++) {
			hobbyList.add(hobbys[i]);
		}
		req.setAttribute("hobbyList", hobbyList);
		req.getRequestDispatcher("editInfo.jsp").forward(req, resp);
	}

	private void clearUserCookie(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * �û��˳���½���ܵ� ��������
		 */
		String account = req.getParameter("account");
		if (account == null || "".equals(account)) {
			resp.sendRedirect("error?oper=err");
			return;
		}
		Cookie cookies[] = req.getCookies();
		if (cookies == null || cookies.length == 0) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		for (Cookie cookie : cookies) {
			
			if("account".equals(cookie.getName())){
				if(account.equals(cookie.getValue())){
					cookie.setMaxAge(0);
					resp.addCookie(cookie);
					break;
				}
			}
		}
		req.getSession().setAttribute("user",null);
		
		resp.sendRedirect("user.s?oper=login");
		return;
	}

	private void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 * ��ҳ ���ݴ��
		 */
		selGoodsByTypeBig(req, resp);
		req.getRequestDispatcher("index.jsp").forward(req, resp);
		return;
	}

	private void info(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 * ��������ҳ������ݴ��
		 */
		String uid = req.getParameter("uid");
		String maxPage = req.getParameter("maxPage");
		String minPage = req.getParameter("minPage");
		
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		// �����û������û���Ϣ
		User user = userService.findUserById(uid);
		req.setAttribute("userInfo", user);
		// �����û��Ĺ�ע��(follow)����˿��(fans)��������(friend)  
		int followNum = userService.countFollow(uid);
		int fansNum = userService.countFans(uid);
		int friendNum = userService.countFriend(uid);
		req.setAttribute("followNum", followNum);
		req.setAttribute("fansNum", fansNum);
		req.setAttribute("friendNum", friendNum);
		// ���Ұ���
		List<String> hList = findHobby(uid);
		req.setAttribute("hList", hList);
		// �����û��Ķ�̬(��������Ʒ)
		// 1����ȡ��ǰҳ��
		String currentPage = req.getParameter("currentPage");
		List<DynamicDiv> DynamicList = goodsService.findDynamic(req, uid, currentPage);
		req.setAttribute("DynamicList", DynamicList);
		req.setAttribute("maxPage", maxPage);
		req.setAttribute("minPage", minPage);
		// ת��
		req.getRequestDispatcher("info.jsp").forward(req, resp);
		return;
	}

	private void vip(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		//��ѯ�û��ϴ���Դ����������Դ��
		String uploadNum = goodsService.countUploadById(uid);
		String downloadNum = goodsService.countDownloadById(uid);
		req.setAttribute("uploadNum", uploadNum);
		req.setAttribute("downloadNum", downloadNum);
		
		req.getRequestDispatcher("vip.jsp").forward(req, resp);
		return;
	}
	private void favorite(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		//��ѯ���û����ղ���Ʒ
		List<Goods> goodsList =  goodsService.findCollection(uid);
		req.setAttribute("goodsList", goodsList);
		//��ѯ�û��ϴ���Դ����������Դ��
		String uploadNum = goodsService.countUploadById(uid);
		String downloadNum = goodsService.countDownloadById(uid);
		req.setAttribute("uploadNum", uploadNum);
		req.setAttribute("downloadNum", downloadNum);
		req.getRequestDispatcher("favorite.jsp").forward(req, resp);
		return;
	}

	private void mySrc(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			req.setAttribute("msg", "���ȵ�¼���ٲ���~~");
			req.getRequestDispatcher("user.s?oper=index").forward(req, resp);
			return;
		}
		//��ѯ�û��ϴ���Դ����������Դ��
		String uploadNum = goodsService.countUploadById(uid);
		String downloadNum = goodsService.countDownloadById(uid);
		req.setAttribute("uploadNum", uploadNum);
		req.setAttribute("downloadNum", downloadNum);
		
		//��ѯ�û��ϴ�����Դ
		List<Goods> goods = goodsService.findGoodsByUid(uid);
		req.setAttribute("goodsList", goods);
		req.getRequestDispatcher("mySrc.jsp").forward(req, resp);
		return;
	}

	private void myDownload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 *  ��ѯ �û�������Դ��¼
		 */
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		
		//��ѯ�û��ϴ���Դ����������Դ��
		String uploadNum = goodsService.countUploadById(uid);
		String downloadNum = goodsService.countDownloadById(uid);
		req.setAttribute("uploadNum", uploadNum);
		req.setAttribute("downloadNum", downloadNum);
		
		//��ѯ�û����ص���Դ
		List<Goods> goods = goodsService.findBuyByUid(uid);
		req.setAttribute("goodsList", goods);
		
		req.getRequestDispatcher("myDownload.jsp").forward(req, resp);
		return;
	}

	private void uploadfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 * �û� �ϴ�ҳ��� ��̬���� ���
		 */
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		List<TypeBig> big = userService.findAllTypeBig();
		List<TypeSmall> small = userService.findAllTypeSmall();
		req.setAttribute("big", big);
		req.setAttribute("small", small);
		req.getRequestDispatcher("uploadfile.jsp").forward(req, resp);
		return;
	}
	

	private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 * ��½
		 */
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {

				// �� ��¼ҳ���ύ�Ĺ����� cookie��Ϣ �鿴�Ƿ�����Լ� ��ǰ��ƺõ� uid
				if ("account".equals(cookies[i].getName())) {
					// ��ѯ���� cookie �е� account ��Ϣ��
					// uid ������� user account
					User user = loginByCookie(cookies[i], cookies[i].getValue());
					if (user == null) {
						req.getRequestDispatcher("login.jsp").forward(req, resp);
						return;
					} else {
						// ��¼�ɹ�
						user.setPwd("");
						
						req.getSession().setAttribute("user", user);//
						resp.sendRedirect("user.s?oper=index&uid=" + user.getUid());
						return;
					}

				}
			}
		}
		req.getRequestDispatcher("login.jsp").forward(req, resp);
		return;
	}

	private List<String> findHobby(String uid) {
		/**
		 *  ���Ұ���
		 */
		List<String> list = userService.findHobby(uid);
		return list;
	}

	private User loginByCookie(Cookie cookies, String account) {
		/**
		 * �û�ͨ��cookie���ܵ�½ 
		 */
		User user = userService.findUserByAccount(account);
		if (user == null) {
			// ��¼ʧ��
			cookies.clone();
			return null;
		} else {
			// ��¼�ɹ�
			return user;

		}
	}

	private void loginservice(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		/**
		 * �û���½����
		 */
		String account = req.getParameter("account");
		String pwd = req.getParameter("pwd");
		System.out.println(account + "   " + pwd);
		if (account == null || "".equals(account)) {
			// �˺�Ϊ��
			req.setAttribute("msg", "�˺Ų���Ϊ�գ�");
			req.getRequestDispatcher("user.s?oper=login").forward(req, resp);
			return;
		}
		if (pwd == null || "".equals(pwd)) {
			// ����Ϊ��
			req.setAttribute("msg", "����д���룡");
			req.getRequestDispatcher("user.s?oper=login").forward(req, resp);
			return;

		}
		User user = userService.findUserByAccountPwd(account, pwd);
		if (user == null) {
			// ��¼ʧ��
			req.setAttribute("msg", "�˺Ż��������");
			req.getRequestDispatcher("user.s?oper=login").forward(req, resp);
			return;
		} else {
			// ��¼�ɹ�
			user.setPwd("");
			if ("on".equals(req.getParameter("check"))) {
				Cookie cookie = new Cookie("account", user.getAccount());
				/*
				 * cookie.setPath("user.s?oper=login"); // url uri
				 * cookie.setPath("user.s?oper=quit");
				 */
				cookie.setMaxAge(60 * 60 * 24 * 7);// ��Ϊ��λ
				resp.addCookie(cookie);
			}
			req.getSession().setAttribute("user", user);//
			resp.sendRedirect("user.s?oper=index&uid=" + user.getUid());
			return;
		}
	}

	private void register(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		/**
		 * �û���ע�Ṧ��
		 */
		req.setCharacterEncoding("utf-8");
		String email = req.getParameter("email");
		String account = req.getParameter("username");
		String pwd = req.getParameter("pwd");
		String uname = req.getParameter("uname");
		String code = req.getParameter("code");
		if (email == null || "".equals(email)) {
			// ����Ϊ��
			req.setAttribute("msg", "���䲻��Ϊ�գ�");
			req.getRequestDispatcher("user.s?oper=register").forward(req, resp);
			return;
		}
		if (uname == null || "".equals(uname)) {
			// ����Ϊ��
			req.setAttribute("msg", "���ֲ���Ϊ�գ�");
			req.getRequestDispatcher("user.s?oper=register").forward(req, resp);
			return;
		}
		if (account == null || "".equals(account)) {
			// �˺�Ϊ��
			req.setAttribute("msg", "�˺Ų���Ϊ�գ�");
			req.getRequestDispatcher("user.s?oper=register").forward(req, resp);
			return;
		}
		if (pwd == null || "".equals(pwd)) {
			// ����Ϊ��
			req.setAttribute("msg", "���벻��Ϊ�գ�");
			req.getRequestDispatcher("user.s?oper=register").forward(req, resp);
			return;
		}
		String ccode = (String) req.getSession().getAttribute("checkcode");
		if (code.equalsIgnoreCase(ccode)) {
			User u=userService.findUserByAccount(account);
			if(u==null){
				List<List<Object>> user = userService.addUser(email, account, pwd, uname);
				if (user == null) {
					// ע��ʧ��
					req.setAttribute("msg", "��������æ��");
					req.getRequestDispatcher("user.s?oper=register").forward(req, resp);
					return;
				} else {
					// ע��ɹ�
					resp.sendRedirect("user.s?oper=login");
				}
			}else{
				req.setAttribute("msg", "���û��Ѿ����ڣ�");
				req.getRequestDispatcher("user.s?oper=register").forward(req, resp);
				return;
			}
			
		} else {
			// ע��ʧ��
			req.setAttribute("msg", "��֤�����");
			req.getRequestDispatcher("user.s?oper=register").forward(req, resp);
			return;
		}

		System.out.println(code);
		System.out.println(ccode);

	}

	private void selGoodsByTypeBig(HttpServletRequest req, HttpServletResponse resp, int bid)
			throws ServletException, IOException {

		/**
		 * ��ѯ ��Ʒ ͨ�� ���� bid ���� 
		 */
		GoodDiv gdiv = userService.findGoodsByTypeBig(bid);
		req.setAttribute("gdiv", gdiv);
		req.setAttribute("bid", bid + "");
	}

	private void selGoodsByTypeBig(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * ��ѯ����typebig���͵� goods
		 */
		
		List<GoodDiv> gooddiv = userService.findGoodsByTypeBig();
		req.setAttribute("gooddiv", gooddiv);
	}

}
