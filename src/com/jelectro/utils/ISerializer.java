package com.jelectro.utils;

import java.io.IOException;

public interface ISerializer {

	public abstract byte[] serialize(Object o) throws IOException;

	public abstract <O> O unserialize(byte[] serial) throws IOException, ClassNotFoundException;

}