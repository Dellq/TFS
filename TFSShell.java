package proj_1;

import java.io.*;
import java.util.*;

public class TFSShell extends Thread  
{
	public TFSShell()
	{
	}
	
	public void run()
	{
		try {
			readCmdLine();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * User interface routine
	 */
	 
	void readCmdLine() throws Exception
	{
		String line, cmd, arg1, arg2, arg3, arg4;
		StringTokenizer stokenizer;
		Scanner scanner = new Scanner(System.in);

		System.out.println("Hal: Good morning, Dave!\n");
		
		while(true) {
			
			System.out.print("ush> ");
			
			line = scanner.nextLine();
			line = line.trim();
			stokenizer = new StringTokenizer(line);
			if (stokenizer.hasMoreTokens()) {
				cmd = stokenizer.nextToken();
				
				if (cmd.equals("mkfs"))
					mkfs();
				else if (cmd.equals("mount"))
					mount();
				else if (cmd.equals("sync"))
					sync();
				else if (cmd.equals("prrfs"))
					prrfs();
				else if (cmd.equals("prmfs"))
					prmfs();
					
				else if (cmd.equals("mkdir")) {
					if (stokenizer.hasMoreTokens()) {
						arg1 = stokenizer.nextToken();
						mkdir(arg1);					
					}
					else
						System.out.println("Usage: mkdir directory");
				}
				else if (cmd.equals("rmdir")) {
					if (stokenizer.hasMoreTokens()) {
						arg1 = stokenizer.nextToken();
						rmdir(arg1);					
					}
					else
						System.out.println("Usage: rmdir directory");
				}
				else if (cmd.equals("ls")) {
					if (stokenizer.hasMoreTokens()) {
						arg1 = stokenizer.nextToken();
						ls(arg1);					
					}
					else
						System.out.println("Usage: ls directory");
				}
				else if (cmd.equals("create")) {
					if (stokenizer.hasMoreTokens()) {
						arg1 = stokenizer.nextToken();
						create(arg1);					
					}
					else
						System.out.println("Usage: create file");
				}
				else if (cmd.equals("rm")) {
					if (stokenizer.hasMoreTokens()) {
						arg1 = stokenizer.nextToken();
						rm(arg1);					
					}
					else
						System.out.println("Usage: rm file");
				}
				else if (cmd.equals("print")) {
					if (stokenizer.hasMoreTokens())
						arg1 = stokenizer.nextToken();
					else {
						System.out.println("Usage: print file position number");
						continue;
					}
					if (stokenizer.hasMoreTokens())
						arg2 = stokenizer.nextToken();
					else {
						System.out.println("Usage: print file position number");
						continue;
					}					
					if (stokenizer.hasMoreTokens())
						arg3 = stokenizer.nextToken();
					else {
						System.out.println("Usage: print file position number");
						continue;
					}	
					try {
						print(arg1, Integer.parseInt(arg2), Integer.parseInt(arg3));
					} catch (NumberFormatException nfe) {
						System.out.println("Usage: print file position number");
					}			
				}
				else if (cmd.equals("append")) {
					if (stokenizer.hasMoreTokens())
						arg1 = stokenizer.nextToken();
					else {
						System.out.println("Usage: append file number");
						continue;
					}
					if (stokenizer.hasMoreTokens())
						arg2 = stokenizer.nextToken();
					else {
						System.out.println("Usage: append file number");
						continue;
					}					
					try {
						append(arg1, Integer.parseInt(arg2));
					} catch (NumberFormatException nfe) {
						System.out.println("Usage: append file number");
					}			
				}
				else if (cmd.equals("cp")) {
					if (stokenizer.hasMoreTokens())
						arg1 = stokenizer.nextToken();
					else {
						System.out.println("Usage: cp file directory");
						continue;
					}
					if (stokenizer.hasMoreTokens())
						arg2 = stokenizer.nextToken();
					else {
						System.out.println("Usage: cp file directory");
						continue;
					}					
					cp(arg1, arg2);
				}
				else if (cmd.equals("rename")) {
					if (stokenizer.hasMoreTokens())
						arg1 = stokenizer.nextToken();
					else {
						System.out.println("Usage: rename src_file dest_file");
						continue;
					}
					if (stokenizer.hasMoreTokens())
						arg2 = stokenizer.nextToken();
					else {
						System.out.println("Usage: rename src_file dest_file");
						continue;
					}					
					rename(arg1, arg2);
				}
					
				else if (cmd.equals("exit")) {
					exit();
					System.out.println("\nHal: Good bye, Dave!\n");
					break;
				}
				
				else
					System.out.println("-ush: " + cmd + ": command not found");
			}
		}
		
		
	}


/*
 * You need to implement these commands
 */
 	
	void mkfs() throws IOException
	{
		// use  tfs_mkfs()
		if(TFSFileSystem.tfs_mkfs()==0) {
			System.out.println("created TFSDiskFile Successfully!!");
		}else {
			System.out.println("Could NOT creat TFSDiskFile !!");
		}
		
		return;
	}
	
	void mount() throws ClassNotFoundException, IOException
	{
		if(TFSFileSystem.tfs_mount()==0) {
			System.out.println("FAT & PCB mounted Successfully!!");
		}else {
			System.out.println("Could NOT mount FAT & PCB !!");
		}
		return;
	}
	
	void sync()
	{
		 if(TFSFileSystem.tfs_sync()==0) {
				System.out.println("FAT & PCB SYNCED Successfully!!");
			}else {
				System.out.println("Could NOT SYNC FAT & PCB !!");
			}
		return;
	}
	
	void prrfs() throws IOException, Exception
	{
		
		if(TFSFileSystem.tfs_prrfs()!=null) {
			System.out.println("The File system is not empty!!");
		}else {
			System.out.println("The File system is empty!!");
		}
		return;
	}
	
	void prmfs() throws IOException, Exception
	{
		
		if(TFSFileSystem.tfs_prmfs()!=null) {
			System.out.println("The File system is not empty!!");
		}else {
			System.out.println("The File system is empty!!");
		}
		return;
	}
	void mkdir(String directory) throws IOException
	{
		String name = directory;
		int nlength = name.length();
		TFSFileSystem.tfs_create_dir(name.getBytes(),name.length());
		return;
	}
	
	void rmdir(String directory)
	{
		String name = directory;
		int nlength = name.length();
		TFSFileSystem.tfs_delete_dir(name.getBytes(),name.length());
		return;
	}
	
	void ls(String directory)
	{
		String name = directory;
		int nlength = name.length();
		TFSFileSystem.tfs_list_dir(name.getBytes(),name.length());
		return;
	}
	
	void create(String file) throws IOException
	{
		String name = file;
		int nlength = name.length();
		TFSFileSystem.tfs_create(name.getBytes(),name.length());
		return;
	}
	
	void rm(String file)
	{
		String name = file;
		int nlength = name.length();
		TFSFileSystem.tfs_delete(name.getBytes(),name.length());
		return;
	}
	
	void print(String file, int position, int number)
	{
		String name = file;
		int nlength = name.length();
		return;
	}
	
	void append(String file, int number)
	{
		String name = file;
		int nlength = name.length();
		return;
	}
	
	void cp(String file, String directory)
	{
		return;
	}
	
	void rename(String source_file, String destination_file)
	{
		String name1 = source_file;
		int nlength1 = name1.length();
		String name2 = destination_file;
		int nlength2 = name2.length();
		TFSFileSystem.tfs_rename(name1.getBytes(),name1.length(),name2.getBytes(),name2.length());
		return;
	}
	
	void exit()
	{
		TFSFileSystem.tfs_exit();
		return;
	}
}


/*
 * main method
 * 
 */

class TFSMain
{
	public static void main(String argv[]) throws InterruptedException
	{
		TFSFileSystem tfs = new TFSFileSystem();
		TFSShell shell = new TFSShell();
		//tfs.TFSFileSystem();
		shell.start();
//		try {
			shell.join();
//		} catch (InterruptedException ie) {}
	}
}
