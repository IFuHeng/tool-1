package persenal.fuheng.bluttooth;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.chuantong.smartlock.bluetooth.timecounter.TimeConterCallback;
import com.chuantong.smartlock.bluetooth.timecounter.TimeCounter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class BaseAiLockManager implements Runnable, TimeConterCallback, GattCallback {
	protected Activity mActivity;
	protected BluetoothManager mBluetoothManager;
	protected BluetoothAdapter mBluetoothAdapter;

	private long mMillisInFuture;
	private long mCountDownInterval;
	/** 计时器 */
	private CountDownTimer mTimeCounter;

	private boolean isConnected;

	/** 监听 */
	protected OnAiLockListener mListener;

	private BluetoothDevice mBluetoothDevice;
	private BluetoothGatt mBluetoothGatt;
	protected BluetoothGattCharacteristic mCharacteristic;

	private byte mState = 0;
	protected static final byte STATE_CONNNECTING = 0;
	protected static final byte STATE_HANDLE_COMMAND = 127;

	/** * 临时输出数组 */
	private ByteArrayOutputStream mByteStream;

	/** 临时输入数组 */
	private byte[] mTempArrayData;
	private int mMark;

	/** 是否通过ui线程反馈 */
	private boolean isCallbackInMainUiTread = true;
	/** 每次写入的组大长度 */
	private static final int MAX_OF_WRITE_SIZE = 20;

	@SuppressLint("HandlerLeak")
	private Handler mHandle = new Handler() {
		public void handleMessage(Message msg) {
			if (mListener == null)
				return;
			switch (msg.what) {
			case STATE_CONNNECTING: {
				mListener.onConnect(mBluetoothGatt, msg.arg1 == 0, msg.obj.toString());
			}
				break;
			case STATE_HANDLE_COMMAND:
				cancleTimeCount();
				if (msg.arg1 == 0)
					mListener.onCallBack((byte[]) msg.obj);
				else
					mListener.onError(msg.obj.toString());
				break;
			default:
				break;
			}
		}
	};
	private GenericBluetoothGattCallback mGattCallBack;

	public BaseAiLockManager(Activity context, OnAiLockListener listener) {
		mActivity = context;
		mListener = listener;
	}

	public void initialize() throws Exception {
		if (mBluetoothManager == null) { // 获取系统的蓝牙管理器
			mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Activity.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				throw new Exception("Unable to initialize BluetoothManager.");
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			throw new Exception("Unable to obtain a BluetoothAdapter.");
		}
		mBluetoothAdapter.enable();
	}

	public void doSendCommond(byte[] data) {
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}

		startTimeCount(16000, 1000);

		mState = STATE_HANDLE_COMMAND;
		mByteStream = null;
		write(mBluetoothGatt, mCharacteristic, data);
	}

	public void doDisconnect() {
		cancleTimeCount();
		if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null)
			return;

		this.mBluetoothGatt.disconnect();
		this.mBluetoothDevice = null;
		mBluetoothGatt.close();
		mCharacteristic = null;

		// mHandle.sendMessage(mHandle.obtainMessage(STATE_CONNNECTING, -1,
		// NOTICE_DISCONNECTED, null));
		doResponseResult(STATE_CONNNECTING, -1, 0, "断开蓝牙连接");

		mByteStream = null;
	}

	private void write(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] data) {
		if (data.length < MAX_OF_WRITE_SIZE) {
			characteristic.setValue(data);
			characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
			gatt.writeCharacteristic(characteristic);
		} else {
			mTempArrayData = data;
			mMark = 0;
			writePushSequeese(gatt, characteristic);
		}
	}

	private void writePushSequeese(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		int length = Math.min(MAX_OF_WRITE_SIZE, mTempArrayData.length - mMark);

		byte[] tempData = new byte[length];

		System.arraycopy(mTempArrayData, mMark, tempData, 0, length);

		characteristic.setValue(tempData);
		characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
		gatt.writeCharacteristic(characteristic);
	}

	public void doSendCommond(String string) {
		doSendCommond(Utils.dataToByte(string));
	}

	public boolean doConnect(BluetoothDevice bluetoothDevice) throws Exception {
		if (mBluetoothAdapter == null || bluetoothDevice == null)
			throw new Exception("BluetoothAdapter not initialized or unspecified address.");

		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}

		mByteStream = null;

		if (bluetoothDevice == mBluetoothDevice && this.mBluetoothGatt != null) {
			// Log.d("BluetoothLeService",
			// "Trying to use an existing mBluetoothGatt for connection.");
			return mBluetoothGatt.connect();
			// throw new
			// Exception("Trying to use an existing mBluetoothGatt for connection.");
		}
		mState = STATE_CONNNECTING;

		startTimeCount(16000, 1000);

		if (mGattCallBack == null)
			mGattCallBack = new GenericBluetoothGattCallback(this);
		this.mBluetoothDevice = bluetoothDevice;
		mBluetoothGatt = bluetoothDevice.connectGatt(mActivity, false, mGattCallBack);

		if (!mBluetoothGatt.connect())
			throw new Exception(" the connection attempt was initiated unsuccessfully ");

		return true;
	}

	protected BluetoothDevice getBluetoothDevices() {
		return mBluetoothDevice;
	}

	public void setOnAiLockListener(OnAiLockListener listener) {
		mListener = listener;
	}

	protected void startTimeCount(long millisInFuture, long countDownInterval) {
		if (mTimeCounter != null)
			cancleTimeCount();

		mMillisInFuture = millisInFuture;
		mCountDownInterval = countDownInterval;

		mActivity.runOnUiThread(this);
	}

	protected void cancleTimeCount() {
		if (mTimeCounter != null) {
			mTimeCounter.cancel();
			mTimeCounter = null;
		}
	}

	@Override
	public void run() {
		mTimeCounter = new TimeCounter(mMillisInFuture, mCountDownInterval, this).start();
	}

	public boolean isConnected() {
		return isConnected;
	}

	protected void setConnected(boolean isConnected) {
		this.isConnected = isConnected;

		if (isConnected)
			doResponseResult(STATE_CONNNECTING, 0, 0, "连接成功");
		else
			doResponseResult(STATE_CONNNECTING, -1, 0, "连接断开……");
	}

	/**
	 * @param isCallbackInMainUiTread
	 *            是否从主ui线程返回结果
	 */
	public void setCallbackInMainUiTread(boolean isCallbackInMainUiTread) {
		this.isCallbackInMainUiTread = isCallbackInMainUiTread;
	}

	protected void doResponseResult(int what, int arg1, int arg2, Object obj) {
		cancleTimeCount();
		if (mListener == null)
			return;

		if (isCallbackInMainUiTread) {
			mHandle.sendMessage(mHandle.obtainMessage(what, arg1, arg2, obj));
			return;
		}

		switch (what) {
		case STATE_CONNNECTING: {
			mListener.onConnect(mBluetoothGatt, arg1 == 0, obj.toString());
		}
			break;
		case STATE_HANDLE_COMMAND:
			if (arg1 != 0)
				mListener.onError(obj.toString());
			else
				mListener.onCallBack((byte[]) obj);
			break;
		default:
			break;
		}

	}

	public boolean isCurrentConnectedBlueTooth(BluetoothDevice bluetoothDevice) {

		if (mBluetoothDevice != null) {
			if (mBluetoothDevice == bluetoothDevice)
				return true;
		}

		return false;

	}

	protected abstract void handleResult(byte[] data);

	protected abstract boolean isDataCorrect(byte[] data);

	/************** TimeConterCallback ******************/
	@Override
	public void onTimeFinish() {
		Log.w("TimeConterCallback", "onTimeFinish");
		if (mState == STATE_CONNNECTING) {
			mBluetoothGatt.disconnect();
		}
		doResponseResult(mState, -1, 0, "操作超时");
	}

	@Override
	public void onTimeTick(long arg0) {
		// Log.w("TimeConterCallback", "onTick " + (System.currentTimeMillis() -
		// time) + " , " + arg0);
	}

	/************************** GattCallback ******************************/
	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		byte[] arrayOfByte = characteristic.getValue();

		if (arrayOfByte == null || arrayOfByte.length == 0)
			return;

		if (mByteStream == null && isDataCorrect(arrayOfByte)) {
			handleResult(arrayOfByte);
			return;
		}

		// 以上是完整数据，下方是非完整数据
		if (mByteStream == null)// 开始时创建输出字节流
			mByteStream = new ByteArrayOutputStream();

		// 存入数据
		mByteStream.write(arrayOfByte, 0, arrayOfByte.length);

		if (isDataCorrect(mByteStream.toByteArray())) {
			handleResult(mByteStream.toByteArray());
			mByteStream = null;
		}
	}

	@Override
	public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		Log.i("onCharacteristicRead", Utils.parseArrayByte2String(characteristic.getValue(), ' ') + ", " + status);
		if (status == BluetoothGatt.GATT_SUCCESS)
			;
	}

	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		if (mTempArrayData != null) {
			mMark += characteristic.getValue().length;

			if (mMark >= mTempArrayData.length - 1) {
				mTempArrayData = null;
				mMark = 0;
			} else
				writePushSequeese(gatt, characteristic);
		}
	}

	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		Log.i("onConnectionStateChange", status + "," + newState);

		if (status != BluetoothGatt.GATT_SUCCESS) {
			setConnected(false);
			return;
		}

		if (newState == BluetoothGatt.STATE_CONNECTED) {
			mBluetoothGatt.discoverServices();
		}

		else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
			setConnected(false);
			if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null)) {
				Log.w("BluetoothLeService", "BluetoothAdapter not initialized");
				return;
			}
			this.mBluetoothGatt.disconnect();
			mBluetoothGatt.close();
			mBluetoothGatt = null;
		}

	}

	@Override
	public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		Log.i("onReadRemoteRssi", rssi + "," + status);
	}

	@Override
	public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		Log.i("onDescriptorRead", new String(descriptor.getValue()) + "," + status);
	}

	@Override
	public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		Log.i("onDescriptorWrite", Utils.parseArrayByte2String(descriptor.getValue(), ' ') + "," + status);
	}

	@Override
	public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
		Log.i("onReliableWriteCompleted", String.valueOf(status));
	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {

		if (status != BluetoothGatt.GATT_SUCCESS) {
			doResponseResult(mState, status, 0, "没找到服务……");
			return;
		}

		List<BluetoothGattService> list = gatt.getServices();
		for (BluetoothGattService bgs : list) {

			Log.w("BluetoothGattService", bgs.getUuid() == null ? "no uuid" : bgs.getUuid().toString() + ", "
					+ (bgs.getType() == 0 ? "SERVICE_TYPE_PRIMARY" : "SERVICE_TYPE_SECONDARY"));

			List<BluetoothGattCharacteristic> arraybgs = bgs.getCharacteristics();
			for (BluetoothGattCharacteristic bgc : arraybgs) {
				if (isTheWriteBlueGattCaracteristic(bgc)) {
					gatt.setCharacteristicNotification(bgc, true);
					mCharacteristic = bgc;
					return;
				}

			}
		}
		// 如果没连接上，断开连接
		this.mBluetoothGatt.disconnect();
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	protected abstract boolean isTheWriteBlueGattCaracteristic(BluetoothGattCharacteristic bgc);
}
