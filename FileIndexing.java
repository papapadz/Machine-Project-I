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
	private static int fc = 0;
	private static String d; 
	private static String k;
	
	public static void main(String[] args) throws Exception {
		
	Scanner r = new Scanner(System.in);
	
	int flag = 0;
	obj.put("keyword", ""); //create new JSON objects
	obj.put("dir", "");
	
	do {	
		x = 0;
		System.out.print("Enter keyword to find: ");
		
		k = r.next().toLowerCase(); //all input is set to lower case.
		
		System.out.print("Enter directory to search: ");
		String myDir = r.next().toLowerCase();
		
		System.out.println("Search Results:");
		
		File dir = new File(myDir); //creates a new File object
		d = dir.toString();
		long start = System.nanoTime(); //record start time
		
		int i = 1; //counter for JSON Objects 
		if(k.equals(obj.getString("keyword")) && (d.equals(obj.getString("dir")))) {//System.out.println("case 1");
			for(i = 1; i<=obj.getInt("count");i++)
				System.out.println(j_array_key.getString(i));
				
			Long exec = getExecTime(start); // calculate execution time
			System.out.println(obj.get("count")  + " result\\s found in " + exec + " milliseconds");
			
		}
		else
		if(readFileIndex(0)) { //System.out.println("case 2");
			for(i = 1; i<j_array_key.length();i++)
				System.out.println(j_array_key.getString(i));
			
			obj.put("dir", d);			//create new JSON Objects
			obj.put("keyword", k);
			obj.put("paths", j_array_key);
			obj.put("count", j_array_key.length()-1);
			
			Long exec = getExecTime(start);
			System.out.println( (i-1) + " result\\s found in " + exec + " milliseconds");
		}
		else {//System.out.println("case 3");
			search(dir, start); //recursive search
		}
		
		System.out.print("Retry? [1] - YES, [0] - NO: ");
		flag = r.nextInt();
		
	}while(flag!=0);
		
	}
	
	private static void search(File dir, long start) throws Exception {
		obj.remove("paths");
		if(getList(dir)>0) { //if found at least 1 file, create JSON Object 
			obj.put("dir", d);
			obj.put("keyword", k);
			obj.put("paths", j_array_key);
			obj.put("count", x);
			writeFile(); //create text file for indexes
		}
		
		Long exec = getExecTime(start);
		System.out.println( x + " result\\s found in " + exec + " milliseconds");
		
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
	
	private static int getList(final File dir) throws JSONException {
		for (final File filename : dir.listFiles()) {
	        if (filename.isDirectory()) 
	            getList(filename); //recursive search
	        else {
	        	if(readFile(filename.toString())) {	//read file and search for the keyword
	            	x++;							//add to counter and add JSON Object to JSON Array
	            	j_array_key.put(x, filename.toString());
	            	System.out.println(filename);	//display file
	            }
	        }
	    }
		return x; //returns the number of files searched
	}
	
	private static boolean readFile(String path) {
		BufferedReader br = null;
		try {
			String thisLine;
			br = new BufferedReader(new FileReader(path));
			while ((thisLine = br.readLine()) != null) { //reads text file per line
				String[] words = thisLine.split(" "); //stores every word in an Array 
				int i = 0; 
				
				while(i<words.length) {
					
					if(words[i].length()>=k.length()) //compare character count 
						if((words[i]).toLowerCase().contains(k)) // parsing made through CONTAINS function
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

		long duration = (endTime - startTime)/1000000; //result in milliseconds
		return duration;
	}
	
	private static void writeFile() throws Exception{
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
	
	private static boolean readFileIndex(int state) throws Exception, ParseException {
		
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
}
