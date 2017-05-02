package persenal.fuheng.bluttooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

@SuppressLint("NewApi")
public class GenericBluetoothGattCallback extends BluetoothGattCallback {

	private GattCallback mListener;

	public GenericBluetoothGattCallback(GattCallback listener) {
		this.mListener = listener;
	}

	public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		super.onCharacteristicChanged(gatt, characteristic);
		Log.w("关键信息", "onCharacteristicChanged(" + Utils.parseArrayByte2String(characteristic.getValue(), ' ') + ")");
		if (mListener != null)
			mListener.onCharacteristicChanged(gatt, characteristic);
	}

	public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		super.onCharacteristicRead(gatt, characteristic, status);
		Log.w("关键信息", "onCharacteristicRead(" + Utils.parseArrayByte2String(characteristic.getValue(), ' ') + ")");
		if (mListener != null)
			mListener.onCharacteristicRead(gatt, characteristic, status);
	}

	public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		super.onCharacteristicWrite(gatt, characteristic, status);
		Log.w("关键信息", "onCharacteristicWrite(" + Utils.parseArrayByte2String(characteristic.getValue(), ' ') + "," + status + ")");
		if (mListener != null)
			mListener.onCharacteristicWrite(gatt, characteristic, status);
	}

	/**
	 * @param gatt
	 *            GATT client
	 * @param status
	 *            Status of the connect or disconnect operation. GATT_SUCCESS if
	 *            the operation succeeds.
	 * @param newState
	 *            Returns the new connection state. Can be one of
	 *            STATE_DISCONNECTED or STATE_CONNECTED
	 * 
	 */
	public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		super.onConnectionStateChange(gatt, status, newState);
		Log.w("关键信息", "onConnectionStateChange(" + status + "," + newState + ")");
		if (mListener != null)
			mListener.onConnectionStateChange(gatt, status, newState);
	}

	public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		super.onDescriptorRead(gatt, descriptor, status);
		Log.w("关键信息", "onDescriptorRead(" + Utils.parseArrayByte2String(descriptor.getValue(), ' ') + "," + status + ")");
		if (mListener != null)
			mListener.onDescriptorRead(gatt, descriptor, status);
	}

	public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		super.onDescriptorWrite(gatt, descriptor, status);
		if (mListener != null)
			mListener.onDescriptorWrite(gatt, descriptor, status);
	}

	public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		super.onReadRemoteRssi(gatt, rssi, status);
		Log.w("关键信息", "onReadRemoteRssi(" + rssi + "," + status + ")");
		if (mListener != null)
			mListener.onReadRemoteRssi(gatt, rssi, status);
	}

	public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
		super.onReliableWriteCompleted(gatt, status);
		Log.w("关键信息", "onReliableWriteCompleted(" + status + ")");
		if (mListener != null)
			mListener.onReliableWriteCompleted(gatt, status);
	}

	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		super.onServicesDiscovered(gatt, status);
		Log.w("关键信息", "onServicesDiscovered(" + status + ")");
		if (mListener != null)
			mListener.onServicesDiscovered(gatt, status);
	}

}
