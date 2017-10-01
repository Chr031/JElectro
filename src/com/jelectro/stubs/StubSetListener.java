package com.jelectro.stubs;

public interface StubSetListener<St> {
	void onStubReceived(St stub);
	void onStubPathUpdated(St stub);
}