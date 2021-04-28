package projet_sesnum;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.io.File;  // Import the File class


class Md5
{
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        get_hash();
    }
    static String data;
    public static String get_hash() throws NoSuchAlgorithmException, IOException {
        String fileName = "C:\\Users\\Mehdi\\Documents\\Virtual Machines\\Ubuntu 64-bit\\shared\\data.txt";
        File file = new File(fileName);

        byte [] fileBytes = Files.readAllBytes(file.toPath());
        //convertir le tableau de bits en une format hexadécimal - méthode 1
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<fileBytes.length;i++) {
            String hex=Integer.toHexString(0xff & fileBytes[i]);
            if(hex.length()==1) hexString.append('0');
            hexString.append(hex);
        }
        System.out.println("En format hexa : " + hexString.toString());
        return  hexString.toString();
    }
    public static void writedata(String data){
        try {
            String path ="C:\\Users\\Mehdi\\Documents\\Virtual Machines\\Ubuntu 64-bit\\shared\\data.txt";
            File myObj = new File(path);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(data);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}


