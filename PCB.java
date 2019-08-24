package proj_1;

import java.io.Serializable;

public class PCB implements Serializable {
	
	String Root = null;
	int FirstFreeBlock = -1;
	int FATsize= 0;
	int NumOfDataBlocks = 0;
	
	
	public void PCB (String R, int FFB, int FS, int NDB) {
		
		this.Root = R;
		this.FirstFreeBlock = FFB;
		this.FATsize = FS;
		this.NumOfDataBlocks =NDB;
		
	}
	
	// a function to print PCB
	public void print (PCB PCB) {
		
		System.out.println("PCB : \n");
		System.out.println("Root : "+PCB.Root);
		System.out.println("First Free Block : "+PCB.FirstFreeBlock);
		System.out.println("Size of FAT : "+PCB.FATsize);
		System.out.println("Number Of Data Blocks : "+PCB.NumOfDataBlocks);
		
	}
	public void setRoot(String R) {
		this.Root = R;
	}
	

}
