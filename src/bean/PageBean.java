package bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LX
 * @date 2018/11/17 - 19:47
 */
public class PageBean {
	//��ǰҳ
	private Integer currentPage;
	//���ж���ҳ
	private Integer totalPage;
	//��������¼
	private Integer totalCount;
	//��ǰҳ����
	private List<?> goodsList = new ArrayList<>();
	
	
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public List<?> getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(List<?> goodsList) {
		this.goodsList = goodsList;
	}
	
	
	
	
}
