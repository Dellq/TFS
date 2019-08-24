package proj_1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import proj_1.FAT;
import proj_1.ObjectSizeFetcher;

public class FAT implements Serializable{

	int Blocknum;
	int Busy = 0;
	
	// FAT entry function.
	public FAT(int state,int Bnum) {
		this.Blocknum = Bnum;
		this.Busy = state;
		
	}
	
	// Updating FAT entries.
	public static void UpdateFATEntery(FAT F, int state,int Bnum) {
		F.Blocknum = Bnum;
		F.Busy = state;
		
		UpdateFreeList(Fat.indexOf(F), state);
	}
	
	// A function to create a FAT list. 
	public static ArrayList<FAT> CreatFAT(int NumBlocks) {
		FAT F;
		ArrayList<FAT> fat = new ArrayList<FAT>();
		if (NumBlocks>=fat.size()) {
			for (int i =0;i<=NumBlocks;i++) {
				F= new FAT(0,-2);
				fat.add(F);
			}
			
		}
		return fat;
	}
	
	// Add another FAT element to the FAT list.
	public static void addFATEntery(ArrayList<FAT> al ) {
		FAT F;
		F= new FAT(0,-2);
		al.add(F);
		UpdateFreeList(al.indexOf(F),0);
		
	}
	
	// A function to allocate space in the for a file/directory.
	public static boolean allocate(int first, int size) {
		
		int block;
		double blocks = (double) size/128;
		
		if (blocks == 0) {
			block = 1;
		}else {
			block =(int) Math.ceil(blocks);
		}
		
		if (block>FL.size()) {

			return false;
		}else {
		
			int [] link = new int[block];
	
			link[0]=first;
			UpdateFreeList(link[0],1);
	
			for (int i =1;i<=block-1;i++) {
				link[i]=FL.pollFirst();
				UpdateFreeList(link[i],1);
				//break;
			}
		
			for (int i =0;i<=block-1;i++) {
				
				if (i == block-1) {
					UpdateFATEntery(Fat.get(link[i]),1,-1);
				
				}else {
					UpdateFATEntery(Fat.get(link[i]),1,link[i+1]);
				}
			}
		return true;
		}
	}
	
	// A function to deallocate the used space after deleting a 
	// file or a directory.
	public static void deallocate(int first,int size) {
		int block;
		double blocks = (double) size/128;

		block =(int) Math.ceil(blocks);
		
		int next = Fat.get(first).Blocknum;
		int temp =0;
		
		UpdateFATEntery(Fat.get(first),0,-2);
		
		for (int i =0;i<=block-1;i++) {
			if (next == -1) {
			
			}else {
				temp =Fat.get(next).Blocknum;
				UpdateFATEntery(Fat.get(next),0,-2);
				next = temp;
			}
		}
	}
	
	// A function to create the free list.
    public static LinkedList<Integer> FreeList(ArrayList<FAT> F) {
    	
    	LinkedList<Integer> freeBlocks = new LinkedList<Integer>();
       
        for (FAT file : F) {
        
            if (file.Busy == 0)
            	
            	freeBlocks.add(F.indexOf(file)); 
        }

        return freeBlocks;
    }
    
    // A function to update the free list.
    public static void UpdateFreeList(int block, int action) {
    	
    	int b = new Integer(block);
    	Object o = block;
    	
    	// add to list
    	if (action == 0) {
    		
    		FL.add(block);
    		
    	// delete from list 	
    	}else if (action == 1) {
    		
    		FL.remove(o);
    	}
    	
    	if (FL.peekFirst()!=null) {
    		First = FL.peekFirst();
    	}
    	
    }
    // A function to list all the elements in the FAT list with the block nubers
    // and if it was in a used state (busy state) or free.
    public static void list(ArrayList<FAT> fat) {
    	
    	for (int i =0;i<fat.size();i++) {
        	System.out.printf("%5d %10s %10s\n", i, fat.get(i).Busy,fat.get(i).Blocknum);
    
        }
    }
    // A function to get the first element from the free list 
    // the first empty block
    public static Integer getFirst() {
    	int val;

    	if (FL.size()==0) {
    		System.out.println("The FAT is FULL!!"); 
    		 val = -1;
    	}else {
    		val = FL.pollFirst();
    		UpdateFATEntery(Fat.get(val),1,-1);
    		
    	}
		return val; 
    }
    
    // A function to allocate and write into the disk in one go
    // makes it easy to write add things ** needs more work **
    public static boolean Disk_allocate(int first, int size) {
		int block;
		double blocks = (double) size/128;
		if (blocks == 0) {
			block = 1;
		}else {
			block =(int) Math.ceil(blocks);
		}

		int [] link = new int[block];

		link[0]=first;
		UpdateFreeList(link[0],1);
		
		for (int i =1;i<=block-1;i++) {

			link[i]=FL.pollFirst();
			UpdateFreeList(link[i],1);
			// write into disk for every block size bytes 
			// so buffer will be that size 
		}
		for (int i =0;i<=block-1;i++) {
			
			if (i == block-1) {
				UpdateFATEntery(Fat.get(link[i]),1,-1);
			
			}else {
				UpdateFATEntery(Fat.get(link[i]),1,link[i+1]);
			}
		}
		
		byte[] buf = null;
		try {
			buf = ObjectSizeFetcher.getBytesFromList(Fat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Buf size =" +buf.length);
		B = buf;
		
		for (int i =0;i<=block-1;i++) {
			int from = (i*128);
			int to = (from+127);
			byte [] range = java.util.Arrays.copyOfRange(buf,from ,to );
			
			if (i == block-1) {
				
				System.out.println("Writing FAT : "+TFSDiskInputOutput.tfs_dio_write_block(i,range)+" Block #: "+i);
			}else {
				
				System.out.println("Writing FAT : "+TFSDiskInputOutput.tfs_dio_write_block(i,range)+" Block #: "+i);
			}
		}
		return true;
		
		}	

	public static byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}
	static byte[] B; 
    static ArrayList<FAT> Fat ;
    static LinkedList<Integer> FL = new LinkedList<Integer>();
    static int First;
	


}
