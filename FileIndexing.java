import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class FileIndexing {

	public static int x = 0;
	
	public static void main(String[] args) {
	
		Scanner r = new Scanner(System.in);
		
		System.out.print("Enter keyword to find: ");
		String keyword = r.next();
		
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
					if(k.equals(words[i]))
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
	
}
