package com.green.kinsomy.amaplocation;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * AmapLocationPlugin
 */
public class AmapLocationPlugin implements MethodChannel.MethodCallHandler, EventChannel.StreamHandler {
	private static final String TAG = "AmapLocationPlugin";
	private String mCity;
	private EventChannel.EventSink mEventSink;
	//声明AMapLocationClient类对象
	private AMapLocationClient mLocationClient = null;
	private PluginRegistry.Registrar registrar;
	//异步获取定位结果
	private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (amapLocation != null) {
				if (amapLocation.getErrorCode() == 0) {
					mCity = amapLocation.getCity();
					mEventSink.success(mCity);
					Log.d("onLocationChanged", amapLocation.toString());
				}
			}
		}
	};

	/**
	 * Plugin registration.
	 */
	public static void registerWith(Registrar registrar) {
		final MethodChannel methodChannel = new MethodChannel(registrar.messenger(), "plugin.kinsomy.com/methodchannel");
		final EventChannel eventChannel = new EventChannel(registrar.messenger(), "plugin.kinsomy.com/eventchannel");
		final AmapLocationPlugin instance = new AmapLocationPlugin(registrar);
		methodChannel.setMethodCallHandler(instance);
		eventChannel.setStreamHandler(instance);
	}

	AmapLocationPlugin(Registrar registrar) {
		this.registrar = registrar;
		//初始化定位
		mLocationClient = new AMapLocationClient(registrar.context());

		//设置定位回调监听
		mLocationClient.setLocationListener(mAMapLocationListener);
	}

	@Override
	public void onMethodCall(MethodCall call, Result result) {
		if (call.method.equals("startLocation")) {
			//启动定位
			mLocationClient.startLocation();
		} else if (call.method.equals("stopLocation")) {
			//停止定位
			mLocationClient.stopLocation();
		} else if (call.method.equals("getCity")) {
			result.success(mCity);
		} else {
			result.notImplemented();
		}
	}

	@Override
	public void onListen(Object o, EventChannel.EventSink eventSink) {
		this.mEventSink = eventSink;
	}

	@Override
	public void onCancel(Object o) {
		mLocationClient.stopLocation();
	}
}
