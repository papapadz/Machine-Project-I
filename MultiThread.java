import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

public class MultiThread implements Runnable {
	
	private String tn;
	private static ArrayList<String> fList = new ArrayList<String>();
	
	MultiThread (String name) {
		tn = name;
	}
	
	public void start() {
		//Thread b = new Thread();
		//b.start();
		run();
	}
	
	@Override
	public void run() {
		try {
			getList(FileIndexing.dir);	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private synchronized void getList(final File dir) throws JSONException, InterruptedException {
		
		for (final File filename : dir.listFiles()) {
			if (filename.isDirectory()) 
				getList(filename); //recursive search
	        else {
	        	if(isRead(filename.toString()))
	    			continue;
	    		else {
	    			updateList(filename.toString());
	    			if(readFile(filename.toString())) {	//read file and search for the keyword
	    				FileIndexing.j_array_key.put(filename.toString());//add to counter and add JSON Object to JSON Array
	    				System.out.println(this.tn+">>> "+filename);	//display file
	            	}
	    		}
	        }
	    }

		//returns the number of files searched
	}
	
	private synchronized static boolean readFile(String path) {
		BufferedReader br = null;
		try {
			char per;
			String thisLine = "";
			int c;
			br = new BufferedReader(new FileReader(path));
			while ((c = br.read()) != -1) { //reads text file per character
				
			if(c == 32 || c == 10 || c == 13 ){
				if(thisLine.trim().toLowerCase().contains(FileIndexing.k))
					return true;
				thisLine = "";
			} else {
				per = (char)c;
				thisLine = thisLine + per;
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

	public static synchronized boolean isRead(String f) throws InterruptedException {
			if(fList.contains(f))
				return true;
		
		return false;
	}

	public synchronized void updateList(String f) throws InterruptedException {
		fList.add(f);
	}

	
}
