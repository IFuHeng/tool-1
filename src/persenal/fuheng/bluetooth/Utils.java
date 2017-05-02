package persenal.fuheng.bluttooth;

public class Utils {

	/**
	 * @param buf
	 * @return 获取校验码
	 */
	public static byte getCheckCode(byte[] buf) {
		int N = buf.length;
		byte result = buf[0x0];
		for (int i = 0x1; i < N; i = i + 0x1) {
			result = (byte) (buf[i] ^ result);
		}
		return result;
	}

	/**
	 * @param data
	 * @return 将可显示字符串转为对应数组
	 */
	public static byte[] dataToByte(String data) {
		byte[] daBy = null;

		if (data == null || data.isEmpty())
			return daBy;

		if (data.length() % 2 == 1) {
			data = '0' + data;
		}

		daBy = new byte[data.length() >> 1];
		for (int i = 0; i < daBy.length; i++) {
			String str = data.substring(i * 2, i * 2 + 2);
			daBy[i] = (byte) Integer.parseInt(str, 16);
		}
		return daBy;
	}

	/**
	 * @param data
	 * @return 字节数组转为可显示字符串
	 */
	public static String bytes2String(byte[] data) {
		String result = new String();
		for (byte b : data)
			result += String.format("%02x", b);
		return result;
	}

	/**
	 * @param data
	 * @param offset
	 * @param length
	 * @return 字节数组中指定部分转为可显示字符串
	 */
	public static String bytes2String(byte[] data, int offset, int length) {
		if (offset >= data.length)
			return null;
		String result = new String();
		length = Math.min(length, data.length - offset);
		for (int i = 0; i < length; i++) {
			result += String.format("%02x", data[offset + i]);
		}
		return result;
	}

	/**
	 * @param value
	 * @return 将int型内容转为byte[4]
	 */
	public static byte[] integer2Bytes(int value) {
		byte[] result = new byte[4];
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) (value >>> (result.length - i - 1 << 3));
		}
		return result;
	}

	/**
	 * @param data
	 * @param offset
	 * @param length
	 *            length ： 1~4
	 * @return 截取length字节内容转为int型
	 */
	public static int bytesToInteger(byte[] data, int offset, int length) {

		length = Math.min(length, 4);

		if (data == null || data.length == 0 || offset + length >= data.length)
			return 0;

		int result = 0;
		for (int i = 0; i < length; i++) {
			result = result << 8 | data[offset + i];
		}
		return result;
	}

	public static short bytesToShort(byte[] data, int offset) {

		if (data == null || data.length == 0 || offset >= data.length - 2)
			return 0;

		return (short) ((data[offset] << 8) | (data[offset + 1] & 0xff));
	}

	/**
	 * @param value
	 * @param separate
	 *            分隔符 ,如果值为0，则不添加分隔符
	 * @return
	 */
	public static String parseArrayByte2String(byte[] value, char separate) {
		if (value == null || value.length == 0)
			return null;

		StringBuilder sb = new StringBuilder();
		for (byte b : value) {
			sb.append(String.format("%02x", b & 0xff));

			if (separate != 0)
				sb.append(separate);
		}
		return sb.toString();

	}
}
