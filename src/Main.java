import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File input = new File(args[0]);

        try{
            // If input file exists, read the file
            Scanner inputReader = new Scanner(input);
            System.out.println(inputReader.nextLine());
            inputReader.close();
        } catch(FileNotFoundException e) {
            // If input file doesn't exist, return an error message and exit
            System.err.println("Error: File does not exist");
            return;
        }
    }
}