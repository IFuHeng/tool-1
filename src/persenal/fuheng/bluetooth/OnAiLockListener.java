package persenal.fuheng.bluttooth;

import android.bluetooth.BluetoothGatt;

public interface OnAiLockListener {

	public void onConnect(BluetoothGatt gatt, boolean isConneced, String returnMsg);

	public void onCallBack(byte[] result);

	public void onError(String error);
}
