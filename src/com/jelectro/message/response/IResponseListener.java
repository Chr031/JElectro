package com.jelectro.message.response;

import com.jelectro.message.Message;

public interface IResponseListener<M extends Message> {

	public void onResponseReceived(Response<M> response);
	
}
