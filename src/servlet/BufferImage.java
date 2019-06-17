package servlet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/bufferImage")
public class BufferImage extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 120;//����ͼƬ�Ŀ��
    public static final int HEIGHT = 30;//����ͼƬ�ĸ߶�
    public static final int WORDS_NUMBER = 4;//��֤�����ַ��ĸ���
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String createTypeFlag = req.getParameter("createTypeFlag");//���տͻ��˴��ݵ�createTypeFlag��ʶ
        //���ڴ��д���һ��ͼƬ
        BufferedImage bi = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_3BYTE_BGR);
        //�õ�ͼƬ
        Graphics g = bi.getGraphics();
        //����ͼƬ�ı���ɫ
        setBackGround(g);
        //����ͼƬ�ı߿�
        setBorder(g);
        //��ͼƬ�ϻ�������
        drawRandomLine(g);
        //��ͼƬ�Ϸ�������ַ�
        String randomString = this.drawRandomNum((Graphics2D)g, createTypeFlag);
        
        //�����������session��
        req.getSession().setAttribute("checkcode", randomString);
        
        //������Ӧͷ֪ͨ�������ͼƬ����ʽ��
        resp.setContentType("image/jpeg");
        
        //������Ӧͷ�����������Ҫ����
        resp.setDateHeader("expries", -1);
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");
        
        //��ͼƬ���������
        ImageIO.write(bi, "jpg", resp.getOutputStream());
    }
    
    //����ͼƬ����ɫ
    //@param g
    private void setBackGround(Graphics g) {
        //������ɫ
        g.setColor(Color.WHITE);
        //�������
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }
    
    /*
     * ����ͼƬ�ı߿�
     * @param g
     * */
    
    private void setBorder(Graphics g) {
        //���ñ߿���ɫ
        g.setColor(Color.BLUE);
        //�߿�����
        g.drawRect(1, 1, WIDTH - 2, HEIGHT -2);
    }
    
    /*
     * ��ͼƬ�ϻ�������� 
     * @param g
     * */
    private void drawRandomLine(Graphics g) {
        //������ɫ
        g.setColor(Color.GREEN);
        //������������������
        for ( int i = 0 ; i < 3 ; i++ ) {
            int x1 = new Random().nextInt(WIDTH);
            int y1 = new Random().nextInt(HEIGHT);
            int x2 = new Random().nextInt(WIDTH);
            int y2 = new Random().nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }
    }
    
    /*
     * ��ͼƬ�ϻ�����ַ�
     * @param g
     * @param createTypeFlag
     * @return String
     * */
    private String drawRandomNum(Graphics g,String createTypeFlag) {
        //������ɫ
        g.setColor(Color.RED);
        g.setFont(new Font("����",Font.BOLD,20));
        
        //������ĸ�����
        String baseNumLetter = "0123456789ABCDEFGHJKLMNOPQRSTUVWXYZ";
        String baseNum = "0123456789";
        String baseLetter = "ABCDEFGHJKLMNOPQRSTUVWXYZ";
        if ( createTypeFlag != null  && createTypeFlag.length() > 0 ) {
            if( createTypeFlag.equals("nl") ) {
                //��ȡ���ֺ���ĸ�����
                return createRandomChar((Graphics2D) g,baseNumLetter);
            } else if ( createTypeFlag.equals("n") ) {
                //��ȡ���ֵ����
                return createRandomChar((Graphics2D) g,baseNum);
            } else if ( createTypeFlag.equals("l") ) {
                //��ȡ��ĸ�����
                return createRandomChar((Graphics2D) g,baseLetter);
            }
        } else {
            //��ȡ���ֺ���ĸ�����
            return createRandomChar((Graphics2D) g,baseNumLetter);
        }
        return "";
    }
    
    /*
     * ��������ַ�
     * @param g
     * @param baseChar
     * @return String
     * */
    private String createRandomChar(Graphics2D g , String baseChar) {
        StringBuffer b = new StringBuffer();
        int x = 5;
        String ch = "";
        for ( int i = 0 ; i < WORDS_NUMBER ; i++ ) {
            //�����������ת�Ƕ�
            int degree = new Random().nextInt() % 30;
            ch = baseChar.charAt(new Random().nextInt(baseChar.length())) + "";
            b.append(ch);
            
            //����Ƕ�
            g.rotate(degree  * Math.PI / 180 , x,20);
            g.drawString(ch, x, 20);
            //����Ƕ�
            g.rotate(-degree  * Math.PI / 180 , x,20);
            x+=30;
        }
        return b.toString();
    }
}