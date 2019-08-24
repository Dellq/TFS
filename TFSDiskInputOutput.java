package proj_1;

import java.io.*;
import java.util.*;

public class TFSDiskInputOutput
{
	/*
	 * Disk I/O API
	 */
	static RandomAccessFile raf;
	static int BLOCK_SIZE = 128 ;
	
	public static int tfs_dio_create(byte[] name, int nlength, int size)
	{
		
        try {

            File f = new File(new String(name, 0, nlength));
            if (!f.exists()) {
                f.createNewFile();

                raf = new RandomAccessFile(f, "rw"); 

                raf.setLength(size * BLOCK_SIZE);



            System.out.println("tfs_dio_create: " + raf.length() + " file created");
            }else {
            	System.out.println("File: " + f.getName()+ "alreday exists");
            }
		} catch (IOException ie) {
			return -1;
		}
		
		return 0;

}

	public static int tfs_dio_open(byte[] name, int nlength)
	{
		
		 try {
			 File f = new File(new String(name, 0, nlength));
	            
	            raf = new RandomAccessFile(f, "rw");
	            
	            
		 } catch (IOException ie) {
			 return -1;
	     }
		 return 0;
	}			
	
	public static int tfs_dio_get_size()
	{
		try {
			raf.length();
		} catch (IOException ie) {
			return -1;
		}
		return 0;
		
	}							
	
	public static int tfs_dio_read_block(int block_no, byte[] buf)
	{
		
		try {
			
			raf.seek(block_no * BLOCK_SIZE);
			raf.read(buf);
			
		} catch (IOException ie) {
			return -1;
		}
		
		return 0;
	}
	
	public static int tfs_dio_write_block(int block_no, byte[] buf)	
	{
		try {
			
			raf.seek(block_no * BLOCK_SIZE);
			raf.write(buf);
			
		} catch (IOException ie) {
			return -1;
		}
		return 0;
	}
	
	public static void tfs_dio_close()		
	{
		try {
			
			raf.close(); 

		} catch (IOException ie) {}
		
	}					
}