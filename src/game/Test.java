package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("saves/output.txt"));
			writer.write("Test 1");
			writer.write("\nTest de salto de linea");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("saves/output.txt"));
			String line;
			while((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
