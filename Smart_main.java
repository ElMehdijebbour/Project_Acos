package projet_sesnum;
import javax.smartcardio.*;
public class Smart_main {
    private static byte [] APDU_IC_CODE = {(byte) 0x80,0x20,0x07,0x00, 0x08, 0x41, 0x43, 0x4F, 0x53, 0x54, 0x45, 0x53, 0x54};
    private static byte [] APDU_PIN_CODE = {(byte) 0x80,0x20,0x06,0x00, 0x08,0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
    /**Constants**/
    private static byte [] PIN_CODE = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
    private  static  int SW_system_files=  0x9000;
    private  static  int SW_user_files=  0x9100;
    public static void main(String[] args) throws CardException {
        //phase1 : Setup
        System.out.println("Phase 1 starting now");
        CardTerminal lecteur = lecteur();
        Card card = check_card(lecteur);
        CardChannel canal = card.getBasicChannel();
        SmartCardAcos smartCard = new SmartCardAcos(lecteur,card,canal,APDU_IC_CODE);
        smartCard.checkIc_Code();
        smartCard.clearCard();
        //phase 2 : write n of files to create to FF02 aka le fichier de personnalisation
        System.out.println("Phase 2 starting now");
        smartCard.selectFile((byte) 0xFF, (byte) 0x02,SW_system_files);
        smartCard.checkIc_Code();
        byte[] vect_command = new byte[9];
        vect_command[0] = (byte) 0x80;
        vect_command[1] = (byte)0xD2;
        vect_command[2] = 0x00; //premier enregistrement
        vect_command[3] = 0x00;
        vect_command[4] = 0x04;
        vect_command[5] = 0x00;
        vect_command[6] = 0x00;
        vect_command[7] = 0x03;   //--> N_OF_File
        vect_command[8] = 0x00;
        smartCard.writeFile(vect_command);
        card.disconnect(true);
        //phase 3 : changing FF04
        System.out.println("Phase 3 starting now");
        card = check_card(lecteur);
        canal = card.getBasicChannel();
        smartCard.setCard(card);
        smartCard.setCanal(canal);
        smartCard.checkIc_Code();
        smartCard.selectFile( (byte) 0xFF, (byte) 0x04,SW_system_files);
        vect_command = new byte[11];
        vect_command[0] = (byte) 0x80;
        vect_command[1] = (byte)0xD2;
        vect_command[2] = 0x00;
        vect_command[3] = 0x00;
        vect_command[4] = 0x06;
        vect_command[5] = 0x20;  // 32 octets
        vect_command[6] = 0x04;  // 4 enregistrements
        vect_command[7] = 0x00;
        vect_command[8] = 0x00;
        vect_command[9] = (byte)0xAA;
        vect_command[10] = 0x10;
        smartCard.writeFile(vect_command);
        //phase 4:
        smartCard.selectFile((byte) 0xAA, (byte) 0x10,SW_user_files);
        String [] file_AA10 = new String [4];
        file_AA10 [0]= " Mr";
        file_AA10 [1]= " El Mehdi Jebbour";
        file_AA10 [2]= " ID200130";
        file_AA10 [3]= " 24/4/2021";
        String [] table = new String [4];
        table [0]= " Titre";
        table [1]= " Nom et Prenom";
        table [2]= " Numero de la carte";
        table [3]= " Date d'expiration";
        System.out.println();
        int i;
        for (i =0; i <4; i ++) {
            byte[] nom_b =  file_AA10 [ i ]. getBytes () ;
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
        for (i =0; i <4; i ++) {
            byte[] read_record ={(byte) 0x80 , (byte) 0xB2 , (byte) i ,
                    0x00 , (byte) file_AA10 [ i ].length() };
            CommandAPDU Read_APDU = new CommandAPDU ( read_record );
            ResponseAPDU rep = canal . transmit ( Read_APDU ) ;
            if ( rep . getSW () == 0x9000 )
            {
                System . out . print (" Ok read file AA10 ,"+table[i]+":");
                System . out . println (new String ( rep . getData () ));
            }
        }
        card . disconnect (true);
        //phase 5: changing password
        card = check_card(lecteur);
        canal = card.getBasicChannel();
        smartCard.setCard(card);
        smartCard.setCanal(canal);
        smartCard.checkIc_Code();
        smartCard.setApduPinCode(APDU_PIN_CODE);
        smartCard.changePinCode(PIN_CODE);
        card.disconnect(true);
        System.out.println("Phase 5 starting now");
        //phase 6 : changing FF04
        System.out.println("FF04");
        card = check_card(lecteur);
        canal = card.getBasicChannel();
        smartCard.setCard(card);
        smartCard.setCanal(canal);
        smartCard.checkIc_Code();
        smartCard.selectFile( (byte) 0xFF, (byte) 0x04,SW_system_files);
        vect_command = new byte[11];
        vect_command[0] = (byte) 0x80;
        vect_command[1] = (byte)0xD2;
        vect_command[2] = 0x00;
        vect_command[3] = 0x00;
        vect_command[4] = 0x06;
        vect_command[5] = (byte) 0x80;  // 128 octets
        vect_command[6] = 0x01;  // 1 enregistrement
        vect_command[7] = 0x00;
        //=1000
        vect_command[8] = 0x00;// security attribute for write
        vect_command[9] = (byte)0xAA;
        vect_command[10] = 0x11;
        smartCard.writeFile(vect_command);
        System.out.println("AA11");
        //
        smartCard.selectFile((byte) 0xAA, (byte) 0x11,SW_user_files);
        String  file_AA11 = "009bd1ba4186ceb2940300000000cbd70a";
        String  label = "signature";
        System.out.println("noice");
        byte[] nom_b =  file_AA11. getBytes () ;
        vect_command =new byte[5+file_AA11.length()];
        vect_command [0]=(byte) 0x80;
        vect_command [1]=(byte) 0xD2;
        vect_command [2]=0x00; //numero d'enregistrement
        vect_command [3]=0x00 ;
        vect_command [4]=(byte) file_AA11.length();
        for ( int j =0; j < nom_b . length ; j ++)
            vect_command [5+ j] = nom_b [j ];
        smartCard.checkIc_Code();
        smartCard.writeFile(vect_command);
        smartCard.checkIc_Code();
        ///////////////////////////////////////////////
        byte[] read_record ={(byte) 0x80 , (byte) 0xB2 , 0x00 ,
                    0x00 , (byte) file_AA11.length() };
        CommandAPDU Read_APDU = new CommandAPDU ( read_record );
        ResponseAPDU rep = canal . transmit ( Read_APDU ) ;
        if ( rep . getSW () == 0x9000 )
            {
                System . out . print (" Ok read file AA11 ,"+label+":");
                System . out . println (new String ( rep . getData () ));
            }
        else System . out . print ("can't read");
    }
    private static Card check_card(CardTerminal lecteur) throws CardException {
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
    private static CardTerminal lecteur(){
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
