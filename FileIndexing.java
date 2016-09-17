import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileIndexing_2 {

	public static int x = 0;
	
	public static void main(String[] args) {
	
		Scanner r = new Scanner(System.in);
		
		System.out.print("Enter keyword to find: ");
		String keyword = r.next().toLowerCase(); //converts the entered keyword to lowercase.
		
		System.out.print("Enter directory to search: ");
		String myDir = r.next();
		
		
		System.out.println("Search Results:");
		
		File dir = new File(myDir);
		long start = System.nanoTime();
		getList(keyword,dir);
		Long exec = getExecTime(start);
		
		System.out.println( x + " result\\s found in " + exec + " milliseconds");
		
	}
	
	private static void getList(String keyword, final File dir) {
		
		for (final File filename : dir.listFiles()) {
	        if (filename.isDirectory()) { 
	            getList(keyword,filename);
	            
	        } else {
	            if(readFile(keyword, filename.toString())) {
	            	x++;
	            	System.out.println(filename);
	            	//Directory where the indexed files are stored
	            	String dir2 = "C:/Users/Rein/Desktop/MIT223/Index Directory";
	            	File indexDir = new File(dir2);
	            	writeFile(keyword, indexDir.toString(),filename.toString());	
	            }
	        }
	    }
	}
	
	private static boolean readFile(String k, String path) {
		BufferedReader br = null;
		try {
			String thisLine;
			br = new BufferedReader(new FileReader(path));
			while ((thisLine = br.readLine()) != null) {
				String[] words = thisLine.split(" ");
				int i = 0; 
				
				while(i<words.length) {
					if(k.equals(words[i].toLowerCase()))
						return true;
					i++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	private static long getExecTime(long startTime) {
		
		long endTime = System.nanoTime();

		long duration = (endTime - startTime)/1000000;
		return duration;
	}
	
	private static String getFileName(File file) {
		String fileName = file.getName();
		if (fileName.indexOf(".") > 0)
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
      	return fileName;
	}
	
	private static void writeFile(String k, String i_path,String k_path){
		BufferedWriter bw = null;
		String delimiter = "=";
		String p = "PATH";
		String p_content = p + delimiter + k_path;
		try{
			File file = new File(i_path + "/" + k + ".txt");
			FileWriter fw = new FileWriter(file,true);
			bw = new BufferedWriter(fw);
			if(!readFileIndex(k_path,file.toString())){
				bw.write(p_content);
		  		bw.newLine();
			}
		}catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally
		{ 
	   		try{
	      		if(bw!=null)
		 			bw.close();
	   		}catch(Exception ex){
	       		System.out.println("Error in closing the BufferedWriter"+ex);
	    	}
		}
	}
	
	private static boolean readFileIndex(String content,String path) {
		BufferedReader br = null;
		try {
			String thisLine;
			br = new BufferedReader(new FileReader(path));
			while((thisLine = br.readLine()) != null){
			    String[] words = thisLine.split("=");
			    if(content.equals(words[1]))
			    	return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}	
}