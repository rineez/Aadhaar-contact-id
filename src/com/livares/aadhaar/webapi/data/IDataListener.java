package com.livares.aadhaar.webapi.data;

import java.util.List;

public interface IDataListener<T> {

	public void onDataLoaded(T loadedData);
	
	public void onDataSaved(T savedData);
	
}
