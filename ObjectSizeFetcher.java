package proj_1;

import java.lang.instrument.Instrumentation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


// ** This code was inspired by multiple Documentation  that I found online **
// ** needs more work ** set the jar file and the manifest file
public class ObjectSizeFetcher {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
    	if (instrumentation == null) {
            throw new IllegalStateException("Agent not initialized.");
        }
        return instrumentation.getObjectSize(o);
    }
	public static byte[] getBytesFromList(List list) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(list);
		out.close();
		return baos.toByteArray();
	}
	public static byte[] getBytesFromObj(Object l) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(l);
		out.close();
		return baos.toByteArray();
	}
	public static List deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return (List) is.readObject();
	}
	public static PCB PCBdeserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return (PCB) is.readObject();
	}
	public static long testObjects(List<FAT> list) throws IOException {

		long size = getBytesFromList(list).length;
		printInUnits(size);
		return size;
	}

	public static byte[] objToByte(Object PCB) throws IOException {
	    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	    ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
	    objStream.writeObject(PCB);
	
	    return byteStream.toByteArray();
	}
	
	public static Object byteToObj(byte[] bytes) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
	    ObjectInputStream objStream = new ObjectInputStream(byteStream);
	
	    return objStream.readObject();
	}
	
	public static void printInUnits(long length) {
		System.out.println("list size is :: " + length / 1000000000 + " GB");
		System.out.println("list size is :: " + length / 1000000 + " MB");
		System.out.println("list size is :: " + length / 1000 + " kB");
		System.out.println("list size is :: " + length + " byte");
	}
}
