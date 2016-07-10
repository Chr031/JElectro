package com.jelectro.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer implements ISerializer {

	/* (non-Javadoc)
	 * @see com.jelectro.utils.ISerializer#serialize(java.lang.Object)
	 */
	@Override
	public byte[] serialize(Object o) throws IOException {
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.flush();
		oos.close();
		final byte[] serial = baos.toByteArray();
		return serial;
	}

	/* (non-Javadoc)
	 * @see com.jelectro.utils.ISerializer#unserialize(byte[])
	 */
	@Override
	public <O> O unserialize(byte[] serial) throws IOException, ClassNotFoundException {
	
		ByteArrayInputStream bais = new ByteArrayInputStream(serial);
		ObjectInputStream ois = new ObjectInputStream(bais);
		O o = (O)ois.readObject();
		ois.close();
		return o;
	}
	
}
