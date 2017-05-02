package persenal.fuheng.bluttooth;

import android.text.Html;

public class GattConnectResult {

	private short orderNum; // 接收消息的命令字 2
	private byte serialNum;// 接收消息的流水号
	private boolean isLocked;// 门锁状态 1 0x00 1 为锁 0 为开
	private boolean isDoorOpened;// 门状态 1 0x00 1 为开 0 为关
	private boolean isAllwaysPowerOn;// 供电类型 1 0x00 0 为常电、1 为接 口供电

	private long ID;// 请求的ID
	private byte requestSeriaNum;// 请求的流水号

	private short requestContentLength;// 请求的内容长度
	private short responseContentLength;// 响应的内容长度

	public short getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(short orderNum) {
		this.orderNum = orderNum;
	}

	public byte getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(byte serialNum) {
		this.serialNum = serialNum;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public boolean isDoorOpened() {
		return isDoorOpened;
	}

	public void setDoorOpened(boolean isDoorOpened) {
		this.isDoorOpened = isDoorOpened;
	}

	public boolean isAllwaysPowerOn() {
		return isAllwaysPowerOn;
	}

	public void setAllwaysPowerOn(boolean isAllwaysPowerOn) {
		this.isAllwaysPowerOn = isAllwaysPowerOn;
	}

	@Override
	public String toString() {
		return "GattConnectResult [接收消息的命令字=" + orderNum + ", 接收消息的流水号=" + serialNum + ", 门锁状态="
				+ (isLocked ? "锁" : "开") + ", 门状态=" + (isDoorOpened ? "开" : "关") + ", 供电类型="
				+ (isAllwaysPowerOn ? "常电" : "接 口供电") + "]";
	}

	public CharSequence toHtmlString() {
		return Html.fromHtml(String.format("门锁状态: %c<br>门状态: %c<br>供电类型: %s", (isLocked ? '锁' : '开'),
				(isDoorOpened ? '开' : '关'), (isAllwaysPowerOn ? "常电" : "接口供电")));
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public byte getRequestSeriaNum() {
		return requestSeriaNum;
	}

	public void setRequestSeriaNum(byte requestSeriaNum) {
		this.requestSeriaNum = requestSeriaNum;
	}

	public short getRequestContentLength() {
		return requestContentLength;
	}

	public void setRequestContentLength(short requestContentLength) {
		this.requestContentLength = requestContentLength;
	}

	public short getResponseContentLength() {
		return responseContentLength;
	}

	public void setResponseContentLength(short responseContentLength) {
		this.responseContentLength = responseContentLength;
	}

}
