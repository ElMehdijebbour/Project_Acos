package projet_sesnum;

import javax.smartcardio.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Smart_main {
    private static byte [] APDU_IC_CODE = {(byte)0x80,(byte)0x20,(byte)0x07,(byte)0x00,(byte)0x08,0x41, 0x43, 0x4F, 0X53, 0x54, 0x45, 0x53, 0x54};

    private static byte [] APDU_PIN_CODE = {(byte) 0x80,0x20,0x06,0x00, 0x08,0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
    /**Constants**/
    private static byte [] PIN_CODE = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
    private  static  int SW_system_files=  0x9000;
    private  static  int SW_user_files=  0x9100;
    public static void main(String[] args) throws CardException, NoSuchAlgorithmException, IOException {
        //phase1 : Setup
        System.out.println("\nPhase 1 starting now: Initialisition de la carte:");
        CardTerminal lecteur = lecteur();
        Card card = check_card(lecteur);
        CardChannel canal = card.getBasicChannel();
        SmartCardAcos smartCard = new SmartCardAcos(lecteur,card,canal,APDU_IC_CODE);
        smartCard.checkIc_Code();
        smartCard.clearCard();
        //phase 2 : write n of files to create to FF02 aka le fichier de personnalisation
        System.out.println("Phase 2 starting now: Personalisation FF02");
        smartCard.setApduIcCode(APDU_IC_CODE);
        smartCard.checkIc_Code();
        smartCard.selectFile((byte) 0xFF, (byte) 0x02,SW_system_files);
        byte[] vect_command={(byte) 0x80, (byte) 0xD2, 0x00, 0x00, 0x04, 0x00, 0x00, 0x03, 0x00};
        smartCard.writeFile(vect_command);
        card.disconnect(true);
        //phase 3 : changing FF04
        System.out.println("\nPhase 3 starting now: Creation des 3 fichiers AA10 AA11 AA12:");
        card = check_card(lecteur);
        canal = card.getBasicChannel();
        smartCard.setCard(card);
        smartCard.setCanal(canal);
        smartCard.checkIc_Code();
        smartCard.CreateFile(0x20,0x04,0x00, 0x00,0x80, (byte) 0xAA,(byte)  0x10);
        byte[] nom_b =  Linker.get_pem();
        String file_AA12 =Linker.getPublicKey();
        smartCard.CreateFile(file_AA12.length(),0x01,0x02, 0x40,0x80, (byte) 0xAA,(byte) 0x12);
        smartCard.CreateFile(0x80,0x01,0x01, 0x00,0x80, (byte) 0xAA,(byte)  0x11);
        System.out.println("\nPhase 4 starting now: Select/write dans le fichiers AA10:");
        smartCard.selectFile((byte) 0xAA, (byte) 0x10,SW_user_files);
        String [] file_AA10 = new String [4];
        file_AA10 [0]= " M";
        file_AA10 [1]= " JebbourDabachFigdal";
        file_AA10 [2]= " ID200130";
        file_AA10 [3]= " 24/06/2023";
        String [] table = new String [4];
        table [0]= " Titre";
        table [1]= " Nom et Prenom";
        table [2]= " Numero de la carte";
        table [3]= " Date d'expiration";
        System.out.println();
        int i;
        for (i =0; i <4; i ++) {
            nom_b = file_AA10[i].getBytes();
            vect_command =new byte[5+file_AA10 [ i ].length()];
            vect_command [0]=(byte) 0x80;
            vect_command [1]=(byte) 0xD2;
            vect_command [2]=(byte) i; //numero d'enregistrement
            vect_command [3]=0x00 ;
            vect_command [4]=(byte) file_AA10 [ i ].length();
            for ( int j =0; j < nom_b . length ; j ++)
                vect_command [5+ j] = nom_b [j ];
            smartCard.writeFile(vect_command);
        }
        String data ="";
        for (i =0; i <4; i ++) {
            byte[] read_record ={(byte) 0x80 , (byte) 0xB2 , (byte) i ,
                    0x00 , (byte) file_AA10 [ i ].length() };
            CommandAPDU Read_APDU = new CommandAPDU ( read_record );
            ResponseAPDU rep = canal . transmit ( Read_APDU ) ;
            if ( rep . getSW () == 0x9000 )
            {
                System . out . print (" Ok read file AA10 ,"+table[i]+":");
                System . out . println (new String ( rep . getData () ));
                data+=new String ( rep . getData () );
            }
        }
        Linker.writedata(data);
        System . out . println ("donnees concatene:" + data);
        card . disconnect (true);
        //phase 5: changing password
        System.out.println("\nPhase 6 starting now: Change Pin code dans FF03 Security File");
        card = check_card(lecteur);
        canal = card.getBasicChannel();
        smartCard.setCard(card);
        smartCard.setCanal(canal);
        smartCard.checkIc_Code();
        smartCard.setApduPinCode(APDU_PIN_CODE);
        smartCard.changePinCode(PIN_CODE);
        card.disconnect(true);
        System.out.println("\nPhase 7 starting now: Select/write dans le fichier AA11");
        card = check_card(lecteur);
        canal = card.getBasicChannel();
        smartCard.setCard(card);
        smartCard.setCanal(canal);
        smartCard.checkIc_Code();
        smartCard.selectFile((byte) 0xAA, (byte) 0x11,0x9101);
        vect_command =new byte[5+nom_b.length];
        vect_command [0]=(byte) 0x80;
        vect_command [1]=(byte) 0xD2;
        vect_command [2]=0x00;
        vect_command [3]=0x00;
        vect_command [4]=(byte) nom_b.length;
        for ( int j =0; j < nom_b . length ; j ++)
            vect_command [5+ j] = nom_b [j ];
        smartCard.checkIc_Code();
        smartCard.writeFile(vect_command);
        System.out.println("\nPhase 8 starting now: Select/write dans le fichiers AA12");
        smartCard.selectFile((byte) 0xAA, (byte) 0x12,0x9102);
        byte[] octetStr = file_AA12.getBytes("UTF-8");
        System.out.println(" "+octetStr.length);
        byte[] nom_c =  file_AA12. getBytes () ;
        vect_command =new byte[5+file_AA12.length()];
        vect_command [0]=(byte) 0x80;
        vect_command [1]=(byte) 0xD2;
        vect_command [2]=(byte) 0x00;
        vect_command [3]=0x00 ;
        vect_command [4]=(byte) file_AA12.length();
        for ( int j =0; j < nom_c . length ; j ++)
            vect_command [5+ j] = nom_c [j ];
        smartCard.writeFile(vect_command);
        smartCard.checkPincode();
        byte[] read_record ={(byte) 0x80 , (byte) 0xB2 , (byte) 0x00 ,
                0x00 , (byte) file_AA12.length() };
        CommandAPDU Read_APDU = new CommandAPDU ( read_record );
        ResponseAPDU rep = canal . transmit ( Read_APDU ) ;
        if ( rep . getSW () == 0x9000 )
        {
            System . out . print (" Ok read file AA12 "+":\n");
            System . out . println (" " + new String(rep.getData()));
        }
        System.out.println(" Done");
    }
    public static Card check_card(CardTerminal lecteur) throws CardException {
        Card card = null;
        // attente de la carte
        while(!lecteur.isCardPresent()){
            System.out.println(" Insérer votre carte ACOS3!!");
        }
        card = lecteur.connect("*");
        if (card !=null){
            System.out.println(" Carte connectee!");
        }
        System.out.println(" Card ATR : "+byteArrayToHexString(card.getATR().getBytes()));
        return card;
    }
    public static CardTerminal lecteur(){
        TerminalFactory tf =TerminalFactory.getDefault();
        CardTerminals lecteurs = tf.terminals();
        CardTerminal lecteur = lecteurs.getTerminal("Gemalto Prox-DU Contact_12400704 0");
        return  lecteur;
    }
    public static String byteArrayToHexString(byte[] b){
        String result = "";
        for ( int i =0; i < b.length ; i ++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100 , 16).substring (1);
        }
        return result ;
    }
}
