package servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSON;
import com.sun.corba.se.impl.orbutil.closure.Constant;

import bean.Board;
import bean.GoodUserDiv;
import bean.Goods;
import bean.Type;
import bean.TypeBig;
import bean.TypeSmall;
import dao.DBHelper;
import service.GoodsService;
import service.TypeBigService;
import service.TypeService;
import service.TypeSmallService;
import service.myException.MyDBException;
import service.serviceImpl.GoodsServiceImpl;
import service.serviceImpl.TypeBigServiceImpl;
import service.serviceImpl.TypeServiceImpl;
import service.serviceImpl.TypeSmallServiceImpl;
import sun.misc.BASE64Decoder;

/**
 * Servlet implementation class GoodsServlet
 */
@WebServlet("/goods.s")
public class GoodsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private GoodsService goodsService = new GoodsServiceImpl();
	private TypeService typeService = new TypeServiceImpl();
	private TypeBigService typeBigService = new TypeBigServiceImpl();
	private TypeSmallService typeSmallService = new TypeSmallServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String oper = req.getParameter("oper");
		if (oper == null || "".equals(oper)) {
			resp.sendRedirect("user.s?oper=index");
			return;
		}

		if ("goods".equals(oper)) {
			goods(req, resp);
		} else if ("uploadfile".equals(oper)) {
			// ��ѯС����
			findTypeSmall(req, resp);
			// ��ѯ���д�����
			findTypeBig(req, resp);
			return;
		} else if ("upload".equals(oper)) {
			upload(req, resp);
		} else if ("delFile".equals(oper)) {
			delFile(req, resp);
		} else if ("cancelFav".equals(oper)) {
			cancelFav(req, resp);
		} else if ("download".equals(oper)) {
			download(req, resp);
		} else if("collect".equals(oper)){
			collect(req,resp);
		} else if("isCollect".equals(oper)){
			isCollect(req,resp);
		}
	}
	/**
	 * �ж��û��Ƿ��ղ�
	 * ck
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void isCollect(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		String gid = req.getParameter("gid");
		long cid = goodsService.findIsCollection(gid,uid);
		if(cid > 0){
			resp.getWriter().append("���ղ�");
		}else{
			resp.getWriter().append("δ�ղ�");
		}
	}
	/**
	 * �ղ�
	 * ck
	 * @param req
	 * @param resp
	 */
	private void collect(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		String gid = req.getParameter("gid");
		long cid = goodsService.addCollection(gid,uid);
		if(cid > 0){
			resp.getWriter().append("�ղسɹ�");
		}else{
			resp.getWriter().append("�ղ�ʧ��");
		}
	}

	/**
	 * ����
	 * ck
	 * @param req
	 * @param resp
	 */
	private void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uid = req.getParameter("uid");
		if (uid == null || "".equals(uid)) {
			resp.sendRedirect("user.s?oper=login");
			return;
		}
		String gid = req.getParameter("gid");
		// �����ݿ��в��Ҫ�����ļ�
		Goods goods = goodsService.findPath(gid);
		// ����·��
		String path = goods.getPath();
		if ("uid+gid".equals(path)) {
			req.setAttribute("msg", "����Դ�ѱ��¼�~~~");
			req.getRequestDispatcher("goods.s?oper=goods&gid="+gid+"&uid="+uid).forward(req, resp);
			return;
		} else {
			// �ļ���
			String fileName = goods.getName();
			fileName = fileName + path.substring(path.lastIndexOf("."));
			//����·��
			path = req.getSession().getServletContext().getRealPath(path);
			//��������������
			resp.setContentType("aplication/x-download");
			//����·�� 
			String fileDownload = path;
			//���ñ����ʽΪUTF-8
			String filedisplay = fileName;  
			filedisplay = URLEncoder.encode(filedisplay,"UTF-8");
			//����Ϊ�Ը�����ʽ����
			resp.addHeader("Content-Disposition", "attachment;filename="+filedisplay);
			
			InputStream is = null;
			OutputStream os = null;
			try{
				is = new FileInputStream(fileDownload);
				os = resp.getOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = is.read(buffer)) != -1){
					os.write(buffer, 0, len);
				}
//				out.clear();
//				out = pageContext.pushBody();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(is != null){
					is.close();
				}
				if(os != null){
					os.close();
				}
				//����û�������Ϣ
				long result = goodsService.addBuy(gid,uid);
			}
		}
	}

	/**
	 * ȡ���ղ�
	 * ck
	 * @param req
	 * @param resp
	 */
	private void cancelFav(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String gid = req.getParameter("gid");
		String uid = req.getParameter("uid");
		int result = goodsService.delCollection(gid);
		if (result > 0) {
			resp.getWriter().append("ȡ���ղسɹ�;" + gid);
		}
	}

	/**
	 * ɾ���ϴ��ļ� ck
	 * 
	 * @param req
	 * @param resp
	 */
	private void delFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String gid = req.getParameter("gid");
		String uid = req.getParameter("uid");
		// 1.ɾ������Ʒ
		int result1 = goodsService.delFile(gid);
		// 2.ɾ������Ʒ������
		int result2 = goodsService.delComment(gid);
		// 3.ɾ������Ʒ���ղ�
		int result3 = goodsService.delCollection(gid);
		// 4.ͳ���û���ǰ���ϴ���
		String num = goodsService.countUploadById(uid);
		if (result1 > 0) {
			resp.getWriter().append("ɾ���ɹ�;" + gid + ";" + num);
		} else {
			resp.getWriter().append("ɾ��ʧ��");
		}
	}

	private void goods(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// ���ĳ����Ʒ
		try {
			int gid = Integer.parseInt(req.getParameter("gid"));
			// ���� gid ȥ��ѯ ��Ʒ����Ϣ
			findByGid(req, resp, gid);
			String path = goodsService.getFileSize(gid);
			path = req.getSession().getServletContext().getRealPath(path);
			long size = new File(path).length()/1024;
			String fileSize = "0KB";
			if(size < 1024){
				fileSize = size + "KB";
			}else{
				fileSize = (size/1024) + "MB";
			}
			req.setAttribute("fileSize", fileSize);
			try {
				List<Board> board = goodsService.getBoard(gid + "");

				req.setAttribute("board", board);
			} catch (MyDBException e) {
				// ���ִ��� ֱ��ת��
				resp.sendRedirect("error?oper=five");
				return;
			}

		} catch (NumberFormatException e) {
			resp.sendRedirect("error?oper=err");
			return;
		}
		req.getRequestDispatcher("goods.jsp").forward(req, resp);
		return;
	}

	/**
	 * ck �ϴ�����
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		String uid = request.getParameter("uid");
		
		Goods goods = new Goods();
		Type type = new Type();

		String filetype = null; // ������
		String fileclass1 = null; // С����1
		String fileclass2 = null; // С����2
		String fileclass3 = null; // С����3

		// ���������ļ��Ĺ�����
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// �����ļ���������
		ServletFileUpload sfu = new ServletFileUpload(factory);
		// ���õ����ļ������ֵ�����ֽ�Ϊ��λ
		sfu.setFileSizeMax(1024 * 1024 * 220);
		List<FileItem> fList;
		try {
			fList = sfu.parseRequest(request);
			for (FileItem fi : fList) {
				// ��ȡԭʼ�ļ���
				String name = fi.getName();
				// ����ı��������
				String fieldName = fi.getFieldName();
				// �Ƿ�����ͨ�ı�
				boolean formField = fi.isFormField();
				// �ֶ�ת������
				String content = fi.getString();
				content = new String(content.getBytes("ISO-8859-1"), "UTF-8");
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
						// д�����
						InputStream bis = new BufferedInputStream(is);
						OutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
						byte[] bs = new byte[1024*8];
						int length = 0;
						while ((length = bis.read(bs, 0, bs.length)) != -1) {
							bos.write(bs, 0, bs.length);
							bos.flush();// ��ջ���������ʹ������������ȫ��д��
						}
						bis.close();
						bos.close();
						
						String filePath = "http://localhost/" + uuid + suffix;
						if ("file".equals(fieldName)) {
							goods.setPath(filePath);
						} else if ("'pic1'".equals(fieldName)) {
							goods.setGimage1(filePath);
						} else if ("'pic2'".equals(fieldName)) {
							goods.setGimage2(filePath);
						} else if ("'pic3'".equals(fieldName)) {
							goods.setGimage3(filePath);
						} else if ("'pic4'".equals(fieldName)) {
							goods.setGimage4(filePath);
						} else if ("'pic5'".equals(fieldName)) {
							goods.setGimage5(filePath);
						} else if ("'pic6'".equals(fieldName)) {
							goods.setGimage6(filePath);
						}
					}
				} else {
					if ("srcname".equals(fieldName)) {
						goods.setName(content);
					} else if ("desc".equals(fieldName)) {
						goods.setContext(content);
					} else if ("uid".equals(fieldName)) {
						goods.setUid(Long.parseLong(content));
					} else if ("filetype".equals(fieldName)) {
						filetype = content;
					} else if ("fileclass1".equals(fieldName)) {
						fileclass1 = content;
					} else if ("fileclass2".equals(fieldName)) {
						fileclass2 = content;
					} else if ("fileclass3".equals(fieldName)) {
						fileclass3 = content;

					}
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(new Date());
			goods.setGdate(Timestamp.valueOf(date));

			Long result1 = goodsService.addGoods(goods);
			// ���ݴ����������Ҷ�Ӧ��bid
			long bid = typeBigService.findIdByTypeName(filetype);
			long sid1 = typeSmallService.findIdByTypeName(fileclass1);
			long sid2 = typeSmallService.findIdByTypeName(fileclass2);
			long sid3 = typeSmallService.findIdByTypeName(fileclass3);
			type.setGid(result1);
			type.setBid(bid);
			type.setSid1(sid1);
			type.setSid2(sid2);
			type.setSid3(sid3);
			Long result2 = typeService.add(type);

			if (result1 > 0 && result2 > 0) {
				response.getWriter().append("�ļ��ϴ��ɹ�");
			} else {
				response.getWriter().append("����æ�����Ժ�����1");
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof FileSizeLimitExceededException) {
				response.getWriter().append("�ϴ�ʧ�ܣ��ļ�����");
			} else {
				response.getWriter().append("����æ�����Ժ�����2");
			}
		}
	}

	/**
	 * ����С���� ck
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void findTypeSmall(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<TypeSmall> typeSmallList = goodsService.findTypeSmall();
		req.setAttribute("typeSmallList", typeSmallList);
	}

	/**
	 * ���Ҵ����� ck
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void findTypeBig(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<TypeBig> typeBigList = goodsService.findTypeBig();
		req.setAttribute("typeBigList", typeBigList);
		req.getRequestDispatcher("uploadfile.jsp").forward(req, resp);
	}

	private void findByGid(HttpServletRequest req, HttpServletResponse resp, int gid)
			throws ServletException, IOException {

		GoodUserDiv gUserDiv = null;
		gUserDiv = goodsService.findByGidUid(gid);

		if (gUserDiv == null) {
			resp.sendRedirect("error?oper=five");
			return;
		}
		/* System.out.println(gUserDiv.toString()); */

		req.setAttribute("good", gUserDiv);

	}
}
