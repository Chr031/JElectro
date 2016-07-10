package com.jelectro.node;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;

import com.jelectro.utils.InstanceCache;

public class NodeKey implements Serializable {

	private static final long serialVersionUID = -332275733090583476L;

	private final String name;

	public String getName() {
		return name;
	}

	public byte[] getId() {
		return id;
	}

	private final byte[] id;

	public NodeKey(String name, byte[] id) {
		this.name = name;
		this.id = id;
	}

	/**
	 * <code>
	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(name.getBytes().length);
		out.write(name.getBytes());
		out.writeInt(id.length);
		out.write(id);
		
	}
	
	
	public void readObject(ObjectInputStream in) throws IOException {
		byte[] b ;
		b = new byte[in.readInt()];
		in.read(b);
		name = new String( b);
		id = new byte[in.readInt()];
		in.read(id);
		
	}</code>
	 */

	/**
	 * TODO definitively need to be commented.
	 * @return
	 * @throws ObjectStreamException
	 */
	Object writeReplace() throws ObjectStreamException {
		return InstanceCache.getRegisteredSingleton(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(id);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeKey other = (NodeKey) obj;
		if (!Arrays.equals(id, other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NodeKey [name=" + name + ", id=" + Arrays.toString(id) + ", hash=" + hashCode() + ", sys hash="
				+ System.identityHashCode(this) + " ]";
	}

}
