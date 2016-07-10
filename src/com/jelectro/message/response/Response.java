package com.jelectro.message.response;

public class Response<M> {

	private final M message;
	private final Throwable error;

	public Response(M message, Throwable error) {
		super();
		this.message = message;
		this.error = error;
	}

	public M getMessage() {
		return message;
	}

	public Throwable getError() {
		return error;
	}

}