package proj_1;

import java.io.*;
import java.util.*;

import proj_1.ObjectSizeFetcher;
import proj_1.FAT;
/*
 * FAT:
 * 
 * I used an Array list structure with all the supporting 
 * functions needed to manage it as shown in the FAT.java file
 * 
 * In The FAT list Print the display shows the block number on the left,
 * the state of the block, on the right it shoes -2 if the block is not connected 
 * to another block, -1 if it is the end of a file/Dir or a number which leads to the 
 * the next block connected to the current block.
 * 
 * I also used a linked list structure for the free list in order to manage 
 * the free space.
 * 
 * Directory:
 * 
 * I created a Directory class and had all the children of the directory 
 * be in an array list for the ease of implementation.
 * In the Directory.java file there are all the supporting functions to manage 
 * this data structure.
 * 
 * PCB:
 * 
 * I created a class to have the information needed for PCB to ease the initialization.
 * 
 * FDT:
 * 
 * I created a new class for FDT and I used an Array list structure to hold all
 * the FDT entries. This will allow for an easy control over the data structure.
 * 
 */
public class TFSFileSystem
{
	 /*
	 * TFS Constructor
	 */
	static Directory Root = new Directory();
	Directory current = Root;
	static String R = "/";
	static PCB PCB = new PCB();
	static int size = 200;
	static byte[] out; 
	static ArrayList<FDT> Fdt;
	
    
	public static void TFSFileSystem() throws IOException
	{
		
		Fdt = new ArrayList<FDT>();
		
		// Create FAT and the free list
		FAT.Fat = FAT.CreatFAT(size);
		FAT.FL = FAT.FreeList(FAT.Fat);
		long S = ObjectSizeFetcher.testObjects(FAT.Fat);
						
		// create PCB				
		PCB.Root = Root.getName();
		PCB.FirstFreeBlock = FAT.FL.peekFirst();
		PCB.FATsize = (int) S;
		PCB.NumOfDataBlocks = (FAT.Fat.size() - FAT.FL.size());
        
		//System.out.println("Hello TFSFileSystem() ");
	}
	
	
	/*
	 * TFS API
	 */
	 
	public static int tfs_mkfs() throws IOException
	{
		// setting the root
		Root.name = R.getBytes();
		Root.isDirectory = 0;
		TFSFileSystem();
		// use the local one.
		String name = "TFSDiskFile";
		int nlength = name.length();
		
		// create or open the file if it already exists (name,nlength,size)
		if(TFSDiskInputOutput.tfs_dio_create(name.getBytes(),nlength,size)==0) {
			if(TFSDiskInputOutput.tfs_dio_open(name.getBytes(), nlength)==0) {
				System.out.println("Opened TFSDiskFile Successfully!!");
			}else {
				System.out.println("Could NOT Open TFSDiskFile !!");
			}
			
			 //Directory.printObjectSize(PCB);
			long c = ObjectSizeFetcher. objToByte(PCB).length;
			
			// 8 bytes for object header
			// 3*4 for 3 integers
			// 3 bytes for padding
			//int P = (8+12+3);
			//System.out.println("PCBsize =" +c);
			//PCB.print(PCB);
			//System.out.println("Fat size before PCB =" +S);
			
			 
			
			// write fat starting at block 1 in disk 
			if(FAT.allocate(1 ,(int)PCB.FATsize)) {
		    //System.out.println("in allocate ########");
			byte[] buf = ObjectSizeFetcher.getBytesFromList(FAT.Fat);
			//System.out.println("Fat byte size =" +buf.length);
			out = buf ;
			System.out.println("Writing FAT : "+TFSDiskInputOutput.tfs_dio_write_block(1,buf));
			}
			// Write PCB in the block 0 in disk
			if(FAT.allocate(0 ,(int) c)) {
				//System.out.println("PCB allocated"+FAT.FL.peekFirst());
				PCB.FirstFreeBlock = FAT.FL.peekFirst();
				PCB.NumOfDataBlocks = (FAT.Fat.size() - FAT.FL.size());
				byte[] bf = ObjectSizeFetcher. objToByte(PCB);
				//System.out.println("PCB allocated"+FAT.FL.peekFirst());
				System.out.println("Writing PCB : "+TFSDiskInputOutput.tfs_dio_write_block(0,bf));
			}
			//FAT.list(FAT.Fat);
			//PCB.print(PCB);
			
			return 0;
		}
		
		return -1;
	}						

	public static int tfs_mount() throws ClassNotFoundException, IOException
	{
		try {
			System.out.println("Reading FAT : "+TFSDiskInputOutput.tfs_dio_read_block(1,out));
			ArrayList<FAT> f = (ArrayList<FAT>) ObjectSizeFetcher.deserialize(out);
			FAT.Fat = f;
			byte[] bf = new byte[120];
			System.out.println("reading BF  : "+TFSDiskInputOutput.tfs_dio_read_block(0,bf));
			PCB P = (PCB) ObjectSizeFetcher.byteToObj(bf);
			PCB = P;
			
		} catch (IOException ie) {
			return -1;
		}
		
		return 0;
	}					

	public static int tfs_umount()
	{
		try {
			byte[] bf = ObjectSizeFetcher. objToByte(PCB);
			System.out.println("Writing PCB : "+TFSDiskInputOutput.tfs_dio_write_block(0,bf));
			
			byte[] buf = ObjectSizeFetcher.getBytesFromList(FAT.Fat);
			System.out.println("Writing FAT : "+TFSDiskInputOutput.tfs_dio_write_block(1,buf));
		} catch (IOException ie) {
			return -1;
		}
		
		return 0;
	}						

	public static int tfs_sync()	
	{
		return tfs_umount();
	}						

	public static String tfs_prrfs() throws Exception, IOException	
	{
		try {
			System.out.println("Reading FAT : "+TFSDiskInputOutput.tfs_dio_read_block(1,out));
			//System.out.println("Reading FAT : "+ObjectSizeFetcher.deserialize(out));
			ArrayList<FAT> f = (ArrayList<FAT>) ObjectSizeFetcher.deserialize(out);
			FAT.list(f);
			byte[] bf = new byte[120];
			//System.out.println("BF 1  : "+bf);
			System.out.println("reading BF  : "+TFSDiskInputOutput.tfs_dio_read_block(0,bf));
			//System.out.println("BF 2  : "+bf);
			PCB P = (PCB) ObjectSizeFetcher.byteToObj(bf);
			//P = (PCB) ObjectSizeFetcher.byteToObj(bf);
			PCB.print(P);
		} catch (IOException ie) {
			return null;
		}
		
		return "good";
	}					

	public static String tfs_prmfs()
	{
		FAT.list(FAT.Fat);
		PCB.print(PCB);
		return "good";
	}

	public static int tfs_open(byte[] name, int nlength)
	{
		/*
		 * Return file descriptor for the file or directory
		 * name has a full path for the file or directory
		 *  Need to search name from the root directory
		 * 
		 */
		String s = (new String(name, 0, nlength));
		Directory F = Directory.FullPathObj(s);
		_tfs_open_fd(F.name, F.isDirectory, (F.firstBlockNo)*128, F.firstBlockNo , F.size);
		int fid = Fdt.indexOf(F);
		if (fid>=0) 
		{
			return fid;
		}
		return -1;
	}			

	public static int tfs_read(int file_id, byte[] buf, int blength)	
	{
		/*
		 * Read blength bytes in buf from fd
		 * Return the number of bytes read
		 */
		return -1;
	}

	public static int tfs_write(int file_id, byte[] buf, int blength)
	{
		// Return the number of bytes written into the file or directory
		return -1;
	}	

	public static int tfs_seek(int file_id, int position)
	{
		
		//Return the new file pointer
		return _tfs_seek_fd(file_id, position);
		
		//return -1;
	}	

	public static void tfs_close(int file_id)
	{
		_tfs_close_fd(file_id);
		return;
	}			

	public static int tfs_create(byte[] name, int nlength) throws IOException
	{
		//Create a file
		// name contains a full path
		byte fflag =1;
		
		String s = (new String(name, 0, nlength));
		String[] parts = s.split("/");
    	int size = parts.length -1;
    	
    	if (size>1) {
			s = Directory.removeElement(parts,size);
			Directory.createFile(Directory.FullPathObj(s),parts[size], fflag,0);
		}else {
			Directory.createFile(Directory.root,parts[size], fflag,0);
		}
		//##createFile(root,String name, byte isDirectory, int size);
		// change the find object to look at a full path one element at a time
		// until it finds the file you want. 
		// ** change for creating a file 
		
		//use the input out put one.
    	//Root =Directory.root;
    	PCB.FirstFreeBlock = FAT.FL.peekFirst();
		PCB.NumOfDataBlocks = (FAT.Fat.size() - FAT.FL.size());
		
		return 1;
	}		
	public static int tfs_rename(byte[] name1, int nlength1,byte[] name2, int nlength2)		
	{
		
		String s1 = (new String(name1, 0, nlength1));
		String s2 = (new String(name2, 0, nlength2));
		
		String[] parts = s2.split("/");
    	int size = parts.length -1;
		if ((Directory.FullPathObj(s1)!=null)&&((Directory.FullPathObj(s2)==null))) {
			Directory.FullPathObj(s1).name = parts[size].getBytes();
			//Root =Directory.root;
			return 1;
			
		}
				
		return -1;
		
	}
	public static int tfs_delete(byte[] name, int nlength)		
	{
		//Delete a file
		//name contains a full path
		String s = (new String(name, 0, nlength));
		Directory.DeleteDirOrFile(Directory.FullPathObj(s));
		//Root =Directory.root;
		PCB.FirstFreeBlock = FAT.FL.peekFirst();
		PCB.NumOfDataBlocks = (FAT.Fat.size() - FAT.FL.size());
		
		return 1;
	}	

	public static int tfs_create_dir(byte[] name, int nlength) throws IOException	
	{
		// Create a directory
		//name contains a full path
		byte Dflag =0;
		
		String s = (new String(name, 0, nlength));
		String[] parts = s.split("/");
    	int size = parts.length -1;
    	
    	if (size>1) {
    		
			String ss = Directory.removeElement(parts,size);
			Directory.createDir(Directory.FullPathObj(ss),parts[size], Dflag);
		}else {
			Directory.createDir(Directory.root,parts[size], Dflag);
		}
    	//Root =Directory.root;
    	PCB.FirstFreeBlock = FAT.FL.peekFirst();
		PCB.NumOfDataBlocks = (FAT.Fat.size() - FAT.FL.size());
		
		return 1;
	}	

	public static int  tfs_read_dir(int fd, byte[] is_directory, byte[] nlength,
			byte[][] name, int[] first_block_no, int[] file_size)
	{
		/*
		 * Read all entries in the directory fd into arrays
		 *  Return the number of entries
		 */
		return -1;
	}
			
	public static int tfs_delete_dir(byte[] name, int nlength)	
	{
		//Delete a directory
		//name contains a full path
		String s = (new String(name, 0, nlength));
		// have to modify delete to only delete iff the dir is empty
		Directory.DeleteDirOrFile(Directory.FullPathObj(s));
		//Root =Directory.root;
		PCB.FirstFreeBlock = FAT.FL.peekFirst();
		PCB.NumOfDataBlocks = (FAT.Fat.size() - FAT.FL.size());
		
		return 1;
	}	
	public static void tfs_list_dir(byte[] name, int nlength)
	{
		//Root =Directory.root;
		String s = (new String(name, 0, nlength));
		Directory.Llist(Directory.FullPathObj(s));
		//Root =Directory.root;
		return ;
	}

	public static void tfs_exit()
	{
		tfs_umount();
		TFSDiskInputOutput.tfs_dio_close();	
		return;
	}
	
	/*
	 * TFS private methods to handle in-memory structures
	 */
	 
 	private static int _tfs_read_block(int block_no, byte buf[])
 	{
 		if(TFSDiskInputOutput.tfs_dio_read_block(block_no,buf)==0) 
 		{
 			return 0;
 		}
 		
 		return -1;
 	}
 	
 	private static int _tfs_write_block(int block_no, byte buf[])
 	{
 		if(TFSDiskInputOutput.tfs_dio_write_block(block_no,buf)==0) 
 		{
 			return 0;
 		}
 		
 		return -1;
 	}
 	private static int _tfs_search_dir(byte[] name, int nlength)
 	{
 		String path = (new String(name, 0, nlength));
 		
 		int bn = Directory.FullPathObj(path).parentBlockNo;
 		
 		return bn;
 	}
 	private static int _tfs_open_fd( byte[] Name, byte isDirectory, int FilePointer, int BlockNum, int size )
 	{
 		FDT F;
		F= new FDT (Name,isDirectory,FilePointer, BlockNum, size );
		Fdt.add(F);
		int index = Fdt.indexOf(F);
		if (index>=0) 
		{
			return index;
		}
		return -1;
 	}
 	
 	private static int _tfs_seek_fd(int fd, int offset)
 	{
 		Fdt.get(fd).FilePointer = offset;
 		return 0;
 	}
 	
 	private static void _tfs_close_fd(int fd)
 	{
 		Fdt.remove(fd);
 		return;
 	}
 	
 	private static int _tfs_get_block_no_fd(int fd, int offset)
 	{
 		
 		return -1;
 	}
 	void _tfs_write_pcb()
 	{
 		
 	}
 	
 	void _tfs_read_pcb()
 	{
 		
 	}

}