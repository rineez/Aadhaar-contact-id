package com.livares.aadhaar.webapi.data;

public abstract class WebData<T> {

	protected IDataListener<T> dataListener;

	public WebData() {}
	
	public IDataListener<T> getDataListener() {
		return dataListener;
	}
	public void setDataListener(IDataListener<T> dataListener) {
		this.dataListener = dataListener;
	}
	
}
