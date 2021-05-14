package projet_sesnum;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.io.File;  // Import the File class
class Linker
{
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        get_hash();
    }
    public static String get_hash() throws NoSuchAlgorithmException, IOException {
        String fileName = "src\\projet_sesnum\\signature.bin";
        File file = new File(fileName);
        byte [] fileBytes = Files.readAllBytes(file.toPath());
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<fileBytes.length;i++) {
            String hex=Integer.toHexString(0xff & fileBytes[i]);
            if(hex.length()==1) hexString.append('0');
            hexString.append(hex);
        }
        System.out.println("En format hexa : " + hexString.toString());
        return  hexString.toString();
    }
    public static byte[] get_pem() throws NoSuchAlgorithmException, IOException {
        String fileName = "src\\projet_sesnum\\security\\signature\\signature.bin";
        File file = new File(fileName);
        byte [] fileBytes = Files.readAllBytes(file.toPath());
        return  fileBytes;
    }
    public static String getPublicKey() throws NoSuchAlgorithmException, IOException {
        String fileName = "src/projet_sesnum/security/keys/keys/rsapublickey.pem";
        File file = new File(fileName);

        String pem = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        String key=new String(pem.substring(pem.indexOf("\n")+1, pem.lastIndexOf("\n-----END PUBLIC KEY-----"))).replace("\n", "");
        return  key;
    }

    public static void writedata(String data){
        try {
            String path ="src\\projet_sesnum\\data\\data.txt";
            File myObj = new File(path);
            if (myObj.createNewFile()) {
                System.out.println(" File created: " + myObj.getName());
            } else {
                System.out.println(" File already exists.");
            }
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(data);
            myWriter.close();
            System.out.println(" Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}


