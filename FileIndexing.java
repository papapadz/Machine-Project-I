import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.*;

public class FileIndexing {

	private static Scanner r = new Scanner(System.in);
	
	public static JSONObject obj = new JSONObject();
	public static JSONArray j_array_key = new JSONArray();
	private static File fn;
	private static int fc = 0;
	private static String d; 
	public static String k;
	private static int pointer = 0;
	public static File dir;
	
	public static void main(String args[]) throws ParseException, Exception {
		startIndex();
	}
	
	public static void startIndex() throws ParseException, Exception {
		
		int flag = 0;
		obj.put("keyword", ""); //create new JSON objects
		obj.put("dir", "");
		
		do {	
			System.out.print("Enter keyword to find: ");
			
			k = r.next().toLowerCase(); //all input is set to lower case.
			
			System.out.print("Enter directory to search: ");
			String myDir = r.next().toLowerCase();
			
			System.out.println("Search Results:");
			
			dir = new File(myDir); //creates a new File object
			d = dir.toString();
			long start = System.nanoTime(); //record start time
			
			if(checkCache()) {
				System.out.println("Searching CACHE. . .");
				for(int i = 1; i<j_array_key.length();i++)
					System.out.println("CACHE>>> "+j_array_key.getString(i));
			}
			else
			if(readFileIndex(0)) { //System.out.println("case 2");
				System.out.println("Searching INDEX FILES. . .");
				for(int i = 1; i<j_array_key.length();i++)
					System.out.println("INDEX FILE>>> "+j_array_key.getString(i));
				
				setObj(d);
				
			}
			else {//System.out.println("case 3");
				obj = new JSONObject();
				j_array_key = new JSONArray();
				System.out.println("Searching DIRECTORY. . .");
				Thread MT1,MT2,MT3,MT4,MT5,MT6,MT7,MT8;			
				
				//start threading
				MT1 = new Thread(new MultiThread("THREAD 1"));
				MT1.start();
				MT2 = new Thread(new MultiThread("THREAD 2"));
				MT2.sleep(200);
				MT2.start();
				MT3 = new Thread(new MultiThread("THREAD 3"));
				MT3.sleep(200);
				MT3.start();
				
				MT1.join();
				MT2.join();
				MT3.join();
				
				if(j_array_key.length()>0) {
					setObj(d);
					writeFile(); //create text file for indexes
				}
			}
			
			
			System.out.println( j_array_key.length() + " result\\s found in " + getExecTime(start) + " milliseconds");
			
			System.out.print("Retry? [1] - YES, [0] - NO: ");
			flag = r.nextInt();
	
		}while(flag!=0);
		
	}
	
	private synchronized static void setObj(String dd) throws Exception {
		FileIndexing.obj.put("dir", dd);
		FileIndexing.obj.put("paths", j_array_key);
		FileIndexing.obj.put("keyword", k);
	}

	private static boolean parseIndexFile() throws JSONException {
	
		BufferedReader br = null; 
		
		try {
				br = new BufferedReader(new FileReader(fn)); //read JSON format array in the index file
				String thisLine = br.readLine();

					JSONObject jo = new JSONObject(thisLine);
					if(jo.get("dir").equals(d)) {	//if the same directory, get all the path 
						j_array_key = jo.getJSONArray("paths"); //save result to JSON Array
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
	
	
	private static synchronized long getExecTime(long startTime) {
		
		long endTime = System.nanoTime();

		long duration = (endTime - startTime)/1000000; //result in milliseconds
		return duration;
	}
	
	static void writeFile() throws Exception{
		String n = k;
		readFileIndex(1); //check the same index file 
		if(fc>0) { //if duplicate file, change filename
			readFileIndex(0);	
			n = n+fc;
		}
		
		File file = new File("C:\\index\\" + n + ".txt"); // create a new file
		
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(obj.toString()); // write the JSON format into file
		}
		
	}
	
	private static synchronized boolean readFileIndex(int state) throws Exception, ParseException {
		
		if(state==1) //1 for counting index files, 0 for reading index files
			fc = 0;
		
		File indexDir = new File("C:\\index"); //default index file save location
			
		for (final File filename : indexDir.listFiles()) {
			String f = filename.getName();
			
			if(f.startsWith(k)) { //check filename
				fn = filename;
				if(state==0) {
					if(parseIndexFile()) //read index file
						return true;
				} else	 
					fc++; //counter for the number of index files with the same filename
			}
		}
	 return false;		
	}
	
	private static boolean checkCache() throws JSONException {
		
		if(obj.get("keyword").equals(k) && obj.get("dir").equals(d)) {	//if the same directory, get all the path 
			j_array_key = obj.getJSONArray("paths"); //save result to JSON Array
			return true;
		}
		
		return false;
	}
	
}