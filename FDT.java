package proj_1;

import java.io.Serializable;
import java.util.ArrayList;

public class FDT implements Serializable {

	byte[] Name;
	byte isDirectory;
	int BlockNum;
	int FilePointer;
	int size;
	
	
	public FDT (byte[] Name, byte isDirectory, int FilePointer, int BlockNum, int size ) {
		this.Name = Name;
		this.isDirectory = isDirectory;
		this.FilePointer = FilePointer;
		this.BlockNum = BlockNum;
		this.size = size; 
	}
	
	public ArrayList<FDT> CreatFDT(){
		
		ArrayList<FDT> fdt = new ArrayList<FDT>();
		
		return fdt;
	}
	
	public static int addFDTEntery(ArrayList<FDT> al, byte[] Name, byte isDirectory, int FilePointer, int BlockNum, int size  ) {
		
		FDT F;
		F= new FDT (Name,isDirectory,FilePointer, BlockNum, size );
		al.add(F);
		return (al.indexOf(F));
		
	}
}
