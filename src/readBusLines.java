import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class readBusLines {
	
	public static void main(String[]args) throws IOException{
		  try{ 
	    FileReader in = new FileReader("busLinesNew.txt");
	    BufferedReader br = new BufferedReader(in);

	    String line;
	    String lineCode;
	    while ((line = br.readLine()) != null) {
	    	lineCode = line.substring(0, line.indexOf(','));  //reading from first letter till first ','
	        System.out.println(lineCode);
	     }
	    in.close();
        }
	    catch (IOException e) {
	        System.out.println("File Read Error");
	    }
     
		
		try{ 
		    FileReader in = new FileReader("busPositionsNew.txt");
		    BufferedReader br = new BufferedReader(in);                 //reading coordinates

		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	
		    	String[] tokens = line.split(",");
		        System.out.println(tokens[3]+" " + tokens[4]);
		    	} 
		    in.close();
	        }
		    catch (IOException e) {
		        System.out.println("File Read Error");
		    }
	     }
}





