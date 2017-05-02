package persenal.fuheng.bluttooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public interface GattCallback {

	/**
	 * @param gatt
	 *            GATT client the characteristic is associated with
	 * @param charactoristic
	 *            Characteristic that has been updated as a result of a remote
	 *            notification event.
	 */
	void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic charactoristic);

	/**
	 * @param gatt
	 *            GATT client invoked
	 *            readCharacteristic(BluetoothGattCharacteristic)
	 * @param characteristic
	 *            Characteristic that was read from the associated remote
	 *            device.
	 * @param status
	 *            GATT_SUCCESS if the read operation was completed successfully.
	 */
	void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

	/**
	 * @param gatt
	 *            GATT client invoked
	 *            writeCharacteristic(BluetoothGattCharacteristic)
	 * @param characteristic
	 *            Characteristic that was read from the associated remote
	 *            device.
	 * @param status
	 *            GATT_SUCCESS if the read operation was completed successfully.
	 */
	void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

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
	void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

	/**
	 * @param gatt
	 *            GATT client invoked readRemoteRssi()
	 * @param rssi
	 *            The RSSI value for the remote device
	 * @param status
	 *            GATT_SUCCESS if the RSSI was read successfully
	 */
	void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);

	/**
	 * @param gatt
	 *            GATT client invoked readDescriptor(BluetoothGattDescriptor)
	 * @param descriptor
	 *            Descriptor that was read from the associated remote device.
	 * @param status
	 *            GATT_SUCCESS if the read operation was completed successfully
	 */
	void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

	/**
	 * @param gatt
	 *            GATT client invoked writeDescriptor(BluetoothGattDescriptor)
	 * @param descriptor
	 *            Descriptor that was writte to the associated remote device.
	 * @param status
	 *            The result of the write operation GATT_SUCCESS if the
	 *            operation succeeds.
	 */
	void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

	/**
	 * @param gatt
	 *            GATT client invoked {@link BluetoothGatt#executeReliableWrite}
	 * @param status
	 *            {@link BluetoothGatt#GATT_SUCCESS} if the reliable write
	 *            transaction was executed successfully
	 */
	void onReliableWriteCompleted(BluetoothGatt gatt, int status);

	/**
	 * @param gatt
	 *            GATT client invoked {@link BluetoothGatt#discoverServices}
	 * @param status
	 *            {@link BluetoothGatt#GATT_SUCCESS} if the remote device has
	 *            been explored successfully.
	 */
	void onServicesDiscovered(BluetoothGatt gatt, int status);

}
