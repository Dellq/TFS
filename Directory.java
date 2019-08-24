package proj_1;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.lang.instrument.Instrumentation;
import proj_1.Directory;
import proj_1.FAT;

public class Directory implements Serializable {
	
	// the total number of entries
	int noEntries; // it has meaning only in the first block.
	int parentBlockNo; // the first block number of the parent dir.
	
    // each entry has
    byte isDirectory; // 0: subdirectory, 1: file
    byte nLength; // name length
    byte reserved1; // reserved
    byte reserved2; // reserved
    byte[] name; // not a full path
    int firstBlockNo; // the first block number
    int size; // the size of the file or subdirectory
    
    Directory parent;
    
    ArrayList<Directory> children = new ArrayList<Directory>();
 
    // a function to get the name of a file or a directory
    public String getName() {
    	String Name = new String(name);
        return Name;//fileName + "." + extension;
    }
    
    //  a function to update the size of the current directory and its
    // parent directories.
    public static void UpdateSize(Directory current, int size ) {
    	//System.out.println("SIZE: "+current.size +" + "+size);
    	Directory curr = null;
    	while (current.parent!=null) {
    		current.size+=size;
    		curr = current;
    		current =current.parent;
    	}
    	//update root size
    	root.size +=size;
    	
    }
    
    // a function to create a directory with allocating the needed space in FAT
    public static boolean createDir(Directory current,String name, byte isDirectory) throws UnsupportedEncodingException {
    	if (current == null) {
    		
    		System.out.println("!!ERORR!! This Folder Does Not Exist ");
    		return false;
    	}else if (find(current, name)) {
    		System.out.println("!!ERORR!! This Folder Already Exists ");
    		return false;
    	}else if (current.isDirectory == 0){
        	if (FAT.FL.size()==0) {
        		//FAT.addFATEntery(FAT.Fat);
        		System.out.println("Cant creat Directory: "+name+" there is not enough space!");
        		//System.out.println(FAT.FL);
        		return false;
				
			}
        	Directory newFile = new Directory();
            if (isDirectory == 0) {
                newFile.name = name.getBytes();
            }
            newFile.isDirectory = isDirectory;
            newFile.parent = current;
            newFile.parentBlockNo = current.firstBlockNo;
        	
            newFile.firstBlockNo = FAT.getFirst();
            current.children.add(newFile);
            System.out.println("Created Directory: "+name+" Successfully!");
            return true;
        } else {
            System.out.println("Can only create a file under a directory");
            return false;
        }
    }
    
    // a function to create a file with allocating the needed space in FAT
    public static boolean createFile(Directory current,String name, byte isDirectory, int size) throws UnsupportedEncodingException {
    	
    	if (current == null) {
    		
    		System.out.println("!!ERORR!! This File Does Not Exist ");
    		return false;
    	}else if (find(current, name)) {
    		System.out.println("!!ERORR!! This File Already Exists ");
    		return false;
    	}else if (current.isDirectory == 0) {
        	
        	//System.out.println("FAT SIZE= "+FAT.FL.size() );
        	
        	if ((FAT.FL.size()<size/128)||(FAT.FL.size()==0)) {

        		System.out.println("Cant creat File: "+name+" "+size+" there is not enough space!");

        		return false;
				
			}
        	Directory newFile = new Directory();
            if (isDirectory == 1) {
                newFile.name = name.getBytes();
                newFile.size=size;
                UpdateSize(current,size);
                //UpdateRootSize
                //System.out.println(root.size);
            }
            newFile.isDirectory = isDirectory;
            newFile.parent = current;
            newFile.parentBlockNo = current.firstBlockNo;
            if (FAT.FL.size()==0) {
            	System.out.println("creating");
            	return false;
				
			}
            newFile.firstBlockNo = FAT.getFirst();
            FAT.allocate(newFile.firstBlockNo,size);
            current.children.add(newFile);
            System.out.println("Created File: "+name+" Successfully!");
            return true;
        } else {
            System.out.println("Can only create a file under a directory");
            return false;
        }
    }
 
    // a function to delete a file or a directory and deallocate the space 
    // that was used by them ( free the blocks ).
    public static void DeleteDirOrFile(Directory current) {
    	
    	if (current == null) {
    		
    		System.out.println("!!ERORR!! This File Does Not Exist ");
    		
    	}else if (current.isDirectory == 1) {
    		
    		System.out.println("It is a File !! ");
    		
    		current.parent.children.remove(current);
    		FAT.deallocate(current.firstBlockNo, current.size);
        	UpdateSize(current.parent,-current.size);
    	
    	}else if (current.children.isEmpty()) {
    		
    		System.out.println("It is an empty Folder !! ");
    		
    		current.parent.children.remove(current);
    		FAT.deallocate(current.firstBlockNo, current.size);
        	UpdateSize(current.parent,-current.size);
        	
    	}else if (!current.children.isEmpty()) {
    		
    		System.out.println("This Folder is NOT EMPTY !! ");
    		
    		/*
    		// Deletes the content of the Directory and the directory
    		DeleteDirChildren(current);
    		
    		current.parent.children.remove(current);
    		FAT.deallocate(current.firstBlockNo, current.size);
        	UpdateSize(current.parent,-current.size);
        	*/
    	}
    	
    }
    
    // a function to delete the children of any directory
    public static void DeleteDirChildren(Directory current) {
    	Directory temp = null;
    	
    	// This works but it does it all in one go
    	// ** need to find a way to take all the block 
    	// numbers from every file and release it**
    	
    	//current.parent.children.remove(current);
    	//UpdateSize(current.parent,-current.size);
    	
 //---------------------------------------------------
    	// works but does not delete the main file/folder
    	// ** FIXED **
    	for (Directory file : current.children) {
    		temp =file;
    		//System.out.println("Now the File is: "+file.getName());
    		if (!file.children.isEmpty()) {
    			DeleteDirChildren(file);
    		}
    	
        	// FAT.list(FAT.Fat);
    	}
    	
    	System.out.println("Now the File is: "+temp.getName());
    	temp.parent.children.remove(temp);
		
		FAT.deallocate(temp.firstBlockNo, temp.size);
    	UpdateSize(temp.parent,-temp.size);

    }
    
    static Directory obj = null;
    // a Function to find a specific object (file/directory) and return that obj.
    public static Directory findObjRec(Directory current,String name) {
    	//obj = null;
        for (Directory file : current.children) {
        	
        	if(file.getName().equals(name)) {
        		obj = file;
        		return obj;

        	}else if (!file.children.isEmpty()) {
        		
        		findObjRec(file,name);
        	}
        	
        }
        return obj;
    }
    
    // this function uses the return from findObjRec and gives the proper output
    // according to the returned value.
    
    public static Directory findObj(Directory current,String name) {
		
    	Directory result = findObjRec(current,name);
    	obj = null;
    	
    	if (result == null) {
    		System.out.println("File/Folder: "+name+" Does Not exist");
    	}
    	
    	return result;  	
    	
    }
    // a function to list the content of a directory and its children.
    public static void list(Directory current) {
    	if (current == null) {
    		
    		System.out.println("!!ERORR!! This File Does Not Exist ");
    		
    	}else { 
	        for (Directory file : current.children) {
	        	System.out.printf("%5d %20s %10s %5s\n", file.size, file.getName(),file.isDirectory,file.firstBlockNo);
	        	if (!file.children.isEmpty()) {
	        		list(file);
	        		}     
	        }
    	}
    }
    
    // a function to find a specific file or directory without returning the 
    // object just a confirmation if it exists or not
    public static boolean find(Directory current,String name) {
        for (Directory file : current.children) {
        	if(file.getName().equals(name)) {
        		System.out.println("found: "+name);
        		return true;
        	}
        	//if (!file.children.isEmpty()) {
        		//find(file,name);
        	//}
        }
        return false;
    }
    // takes a full path and returns the object wanted
    static Directory Result = null;
    public static Directory FullPathObj(String s) {
    	
    	//Directory Result = null;
    	if (s=="/") {
    		Result=root;
    	}else {
	    	String[] parts = s.split("/");
	    	int size = parts.length -1;
			//System.out.println("size : " +size);
	
			if (size>1) {
				s =removeElement(parts,size);
				Result =  findObj(FullPathObj(s),parts[size]);
			}else if (size<0) {
				 return root;
			}else {
				 return findObj(root,parts[size]);
			}
    	}
		return Result;

   }
    // removes the last part of a path and return the rest of the path
    public static  String removeElement( String [] arr, int index ){
    	 // change the array to a list of strings
    	List<String> aList  = new ArrayList(Arrays.asList(arr));
		 
		 aList.remove(index); 
		 int size =aList.size();
		 String [] arr2 = new String [size];
		 aList.toArray(arr2); // convert list back to array
		 String s = String.join("/",arr2);
		 return s;
    }
    // find the file/directory using a full path
    public static Directory PathObj(String val) {
	     String[] parts = val.split("/");
	     int size = parts.length -1;
	     
        return findObj(root,parts[size]);
    }
    
    static String path = "";
    static boolean here = false;
    
    // a function to create/get the path of a file/directory 
    public static String GetPath (Directory current,String name) {

    	for (Directory file : current.children) {	
        	if(file.getName().equals(name)) {
        		if (path.isEmpty()) {

        			path = file.parent.getName();
        		}
        		path = path.concat(file.getName());
        		System.out.println("path: "+path);
        		here= true;

        		return path;
        	}else if (file.isDirectory == flag ) {
        		
        		System.out.println("File:@ "+file.getName());
        		 if (!file.children.isEmpty()) {
	        		//System.out.println("path:@ "+path);
	        		if (path.isEmpty()) {
	        			//System.out.println("else if :"+file.parent.getName());
	        			path = file.parent.getName();
	        		}
	        		path = path.concat(file.getName())+"/";
	        		GetPath(file,name);
	        	}
        	}
        	
        }

    	if (!here) {
    		//System.out.println("Path not found !!!");
    		path = "";
    	}
    	return path;
    }
    public static void Llist(Directory current) {
    	if (current == null) {
    		
    		System.out.println("!!ERORR!! This File Does Not Exist ");
    		
    	}else { 
    		System.out.printf("%10s %5s %5s \n\n","NAME","SIZE","TYPE");
	        for (Directory file : current.children) {
	        	System.out.printf("%10s %5d %5s \n",file.getName(),file.size,file.isDirectory);
	        	     
	        }
    	}
    }

    byte fflag =1;
    static byte flag = 0;
    static Directory root  = new Directory();
    
    

}
