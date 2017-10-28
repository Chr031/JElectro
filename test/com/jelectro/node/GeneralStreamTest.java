package com.jelectro.node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.junit.Before;
import org.junit.Test;

public class GeneralStreamTest {

	private int cpt = 100;
	private NodeKey[] nodeKeys;

	@Before
	public void initNodeKeys() {
		NodeService nodeService = new NodeService();
		nodeKeys = new NodeKey[cpt];
		for (int i = 0; i < cpt; i++) {
			nodeKeys[i] = nodeService.createNodeKey("" + i);
		}
	}

	@Test
	public void ObjectStreamSpeedtest() throws IOException, ClassNotFoundException {

		System.out.println("**********************ObjectStreamSpeedtest**************************");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		long start = System.nanoTime();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		for (int i = 0; i < cpt; i++) {
			oos.writeObject(nodeKeys[i]);

		}
		oos.flush();
		oos.close();
		baos.close();
		long end = System.nanoTime();

		System.out.println("Serialization time : " + (end - start) / 1000000d);
		System.out.println("Ms per object : "  + (end - start) / (1000000d * cpt) );
		
		
		System.out.println("Size : " + baos.toByteArray().length);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		start = System.nanoTime();
		ObjectInputStream ois = new ObjectInputStream(bais);
		int j = 0;
		while (j < cpt) {
			NodeKey nk = (NodeKey) ois.readObject();
			j++;
		}
		ois.close();
		end = System.nanoTime();
		System.out.println("Deserialization time : " + (end - start) / 1000000d);
		System.out.println("Ms per object : "  + (end - start) / (1000000d * cpt) );

	}

	@Test
	public void ObjectStreamSpeedWithCompressiontest() throws IOException, ClassNotFoundException {

		System.out.println("**********************ObjectStreamSpeedWithCompressiontest**************************");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final Deflater def = new Deflater(5);
		DeflaterOutputStream dos = new DeflaterOutputStream(baos, def);

		long start = System.nanoTime();
		ObjectOutputStream oos = new ObjectOutputStream(dos);
		for (int i = 0; i < cpt; i++) {
			oos.writeObject(nodeKeys[i]);
			oos.flush();

		}
		dos.finish();
		oos.close();
		dos.close();
		baos.close();
		long end = System.nanoTime();

		System.out.println("Serialization and compression time : " + (end - start) / 1000000d);
		System.out.println("Size : " + baos.toByteArray().length);
		System.out.println("Compression ratio : " + (double) def.getTotalOut() / def.getTotalIn());
		System.out.println("Ms per object : "  + (end - start) / (1000000d * cpt) );

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		InflaterInputStream iis = new InflaterInputStream(bais);
		start = System.nanoTime();
		ObjectInputStream ois = new ObjectInputStream(iis);
		int j = 0;
		while (j < cpt) {
			NodeKey nk = (NodeKey) ois.readObject();
			j++;
		}
		ois.close();
		end = System.nanoTime();
		System.out.println("Deserialization and decompression time : " + (end - start) / 1000000d);
		System.out.println("Ms per object : "  + (end - start) / (1000000d * cpt) );
	}

	@Test
	public void CompressSocketStreamTest() throws IOException, ClassNotFoundException {

		System.out.println("**********************CompressSocketStreamTest**************************");

		ServerSocket serverSocket = new ServerSocket(14001);

		Thread t = new Thread() {
			public void run() {
				try {
					Socket s = new Socket("localhost", 14001);
					Deflater def = new Deflater(5);
					DeflaterOutputStream dos = new DeflaterOutputStream(s.getOutputStream(), def);
					long start;

					long time = 0;
					ObjectOutputStream oos = new ObjectOutputStream(dos);

					for (int i = 0; i < cpt; i++) {
						start = System.nanoTime();
						oos.writeObject(nodeKeys[i]);
						oos.flush();
						dos.flush();
						System.out.println("w" + i+ " compress ratio " +
						 (double)def.getBytesWritten() / def.getBytesRead() );
						// Thread.sleep(20);
						time += System.nanoTime() - start;
					}

					dos.finish();

					oos.close();
					dos.close();
					System.out.println("Time write : " + time / 1000000d);
					System.out.println("Compression ratio : " + (double) def.getTotalOut() / def.getTotalIn());
					System.out.println("Ms per object : "  + (time) / (1000000d * cpt) );
					s.close();
				} catch (Exception e) {
					
					e.printStackTrace();
				}

			}
		};
		t.start();
		Socket socket = serverSocket.accept();
		InflaterInputStream iis = new InflaterInputStream(socket.getInputStream());
		ObjectInputStream ois = new ObjectInputStream(iis);
		long start;

		long time = 0;

		for (int i = 0; i < cpt; i++) {
			start = System.nanoTime();
			NodeKey nk = (NodeKey) ois.readObject();
			System.out.println("r"+i);
			time += System.nanoTime() - start;
		}

		System.out.println("Time read : " + time / 1000000d);
		System.out.println("Ms per object : "  + (time) / (1000000d * cpt) );
		
		socket.close();
		serverSocket.close();
	}
	
	
	@Test
	public void SuccessiveSerializationTest() {
		System.out.println("**********************SuccessiveSerializationTest**************************");

		
		
	}
	
	

}
