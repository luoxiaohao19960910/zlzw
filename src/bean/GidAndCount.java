package bean;

public class GidAndCount {

	private Long gid; //��¼��Ʒid
	@Override
	public String toString() {
		return "GidAndCount [gid=" + gid + ", number=" + number + "]";
	}
	public Long getGid() {
		return gid;
	}
	public void setGid(Long gid) {
		this.gid = gid;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	private int number; //��¼��ѯ����Ŀ
}
