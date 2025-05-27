package com.river.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;



public class HttpThread extends Thread{

	Handler handler = null;
	String url = null;
	String namespace = null;
	String methodName = null;
	Activity activity=null;
	HashMap<String, Object> params = new HashMap<String, Object>();
	ProgressDialog progressDialog = null;

	//构造函数
	public HttpThread(Handler handler,Activity activity){
		this.handler = handler;
		this.activity=activity;
	}

	/**
	 * 启动线程
	 */
	public void doStart(String url,String namespace,String methodName,
						HashMap<String, Object> params){
		this.url=url;
		this.namespace = namespace;
		this.methodName = methodName;
		this.params = params;
		progressDialog = ProgressDialog.show(activity,
				"提示","正在请求请稍等...", true);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		this.start();
	}
	/**
	 * 线程运行
	 */
	@Override
	public void run() {
		System.out.println("jack");
		super.run();
		try{
			//web service 请求
			SoapObject result= (SoapObject)CallWebService();
			//构造数据
			String value=null;
			if(result!=null&&result.getPropertyCount()>0){
				for(int i=0;i<result.getPropertyCount();i++){
					SoapPrimitive primitive = (SoapPrimitive)result.getProperty(i);
					value = primitive.toString();

				}
				//取消进度框
				progressDialog.dismiss();
				//构造消息

				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString("result", value);
				message.setData(bundle);
				handler.sendMessage(message);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	private Object CallWebService() {
		String SOAP_ACTION = namespace+methodName;
		//创建SoapObject实例
		SoapObject request = new SoapObject(namespace, methodName);
		//生成调用web service 方法的soap请求消息
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		//设置.net web service
		envelope.dotNet = true;

		//请求参数
		if(params!=null&&!params.isEmpty()){
			for(Iterator it=params.entrySet().iterator();it.hasNext();){
				Map.Entry<String, Object> e = (Entry)it.next();
				request.addProperty(e.getKey().toString(),e.getValue());
			}
		}
		//发送请求
		envelope.setOutputSoapObject(request);
		HttpTransportSE transport = new HttpTransportSE(url);
		SoapObject result = null;
		try{
			//web service请求
			transport.call(SOAP_ACTION, envelope);
			result = (SoapObject) envelope.bodyIn;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;

	}
}