import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

import org.json.*;

public class FileIndexing {

	private static int x = 0;
	private static JSONObject obj = new JSONObject();
	private static JSONArray j_array_key = new JSONArray();
	private static File fn;
	
	public static void main(String[] args) throws Exception {
		
	Scanner r = new Scanner(System.in);
	
	int flag = 0;
	obj.put("keyword", "");
	
	do {	
		x = 0;
		System.out.print("Enter keyword to find: ");
		/*no need lower case*/
		String keyword = r.next(); //.toLowerCase(); //converts the entered keyword to lower case.
		
		System.out.print("Enter directory to search: ");
		String myDir = r.next();
		
		System.out.println("Search Results:");
		
		File dir = new File(myDir);
		long start = System.nanoTime();
		
		int i = 1;
		if(keyword.equalsIgnoreCase(obj.getString("keyword"))) {
			for(i = 1; i<=obj.getInt("count");i++)
				System.out.println(j_array_key.getString(i));
				
			Long exec = getExecTime(start);
			System.out.println(obj.get("count")  + " result\\s found in " + exec + " milliseconds");
			
		}
		else
		if(readFileIndex(keyword)) {
			parseIndexFile();
			for(i = 1; i<j_array_key.length();i++)
				System.out.println(j_array_key.getString(i));
			
			Long exec = getExecTime(start);
			System.out.println( (i-1) + " result\\s found in " + exec + " milliseconds");
		}
		else
			search(keyword, dir, start);
		
		System.out.print("Retry? [1] - YES, [0] - NO: ");
		flag = r.nextInt();
		
	}while(flag!=0);
		
	}
	
	private static void search(String keyword, File dir, long start) throws Exception {
		obj.remove("paths");
		if(getList(keyword,dir)>0) {
			//obj.put("directory", dir.toString());
			obj.put("keyword", keyword);
			obj.put("paths", j_array_key);
			obj.put("count", x);
			writeFile(keyword);
		}
		
		Long exec = getExecTime(start);
		System.out.println( x + " result\\s found in " + exec + " milliseconds");
		
	}
	
	private static void parseIndexFile() throws JSONException {
		
		BufferedReader br = null;
		
		try {
				String thisLine;
				br = new BufferedReader(new FileReader(fn));
				thisLine = br.readLine();

					JSONObject jo = new JSONObject(thisLine);
					j_array_key = jo.getJSONArray("paths");
					
	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
	
	private static int getList(String keyword, final File dir) throws JSONException {
		for (final File filename : dir.listFiles()) {
	        if (filename.isDirectory()) 
	            getList(keyword,filename);
	        else {
	        	if(readFile(keyword, filename.toString())) {
	            	x++;
	            	j_array_key.put(x, filename.toString());
	            	System.out.println(filename);	
	            }
	        }
	    }
		return x;
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
					/*made use of ignore case rather than to lower case*/
					if(k.equalsIgnoreCase(words[i]))
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
	
	private static void writeFile(String k) throws Exception{
		File file = new File("C:\\index\\" + k + ".txt");
		
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(obj.toString());
		}
		
	}
	
	private static boolean readFileIndex(String keyword) throws Exception, ParseException {
		
		File indexDir = new File("C:\\index");
			
		for (final File filename : indexDir.listFiles()) {
			String f = filename.getName();
			
			if(f.startsWith(keyword)) {
				fn = filename;
				return true;
			}
		}
			
	 return false;
		
	}	
}
