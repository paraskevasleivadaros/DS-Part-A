import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class readBusLines {

	public static void main(String[] args) throws IOException {
		try {
			String path = Paths.get("src\\busLinesNew.txt").toAbsolutePath().toString();
			// System.out.println(path);
			FileReader in = new FileReader(path);
			BufferedReader br = new BufferedReader(in);

			String line;
			String lineCode;
			while ((line = br.readLine()) != null) {
				lineCode = line.substring(0, line.indexOf(','));  //reading from first letter till first ','
				System.out.println(lineCode);
			}
			in.close();

		} catch (IOException e) {
			System.out.println("File Read Error");
		}
	}
}