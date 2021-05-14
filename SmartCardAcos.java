package projet_sesnum;
import javax.smartcardio.*;
public class SmartCardAcos {
    static CardTerminal lecteur ;
    Card card ;
    static CardChannel canal ;
    /**APDU codes**/
    private static byte [] APDU_IC_CODE ;
    private static byte [] APDU_PIN_CODE;
    /**Pin code**/
    /**Constructor**/
    public SmartCardAcos(CardTerminal lecteur, Card card, CardChannel canal, byte [] APDU_IC_CODE) {
        setLecteur(lecteur);
        setCard(card);
        setCanal(canal);
        setApduIcCode(APDU_IC_CODE);
    }



    public void setCard(Card card) {
        this.card = card;
    }

    public  void setApduIcCode(byte[] apduIcCode) {
        APDU_IC_CODE = apduIcCode;
    }

    public  void setApduPinCode(byte[] apduPinCode) {
        APDU_PIN_CODE = apduPinCode;
    }

    public void setLecteur(CardTerminal lecteur) {
        this.lecteur = lecteur;
    }
    public void setCanal(CardChannel canal) {
        this.canal = canal;
    }

    public static void selectFile(byte one, byte two, int SW) throws CardException {
       byte [] select_command = {(byte)0x80,(byte)0xA4,(byte)0x00,(byte)0x00,(byte)0x02,(byte) one,(byte) two};
        ResponseAPDU rep =submit_APDU(select_command);
        if (rep.getSW() == SW){
            System.out.println(" Ok," + String.format("0x%02X", one)+String.format("%02X", two) + " Selected !");
        }
        else
            System.out.println(" Error, not Selected!(0x" + Integer.toHexString(rep.getSW())+")");
    }
    static  void writeFile(byte[] vect_command) throws CardException {
        ResponseAPDU rep =submit_APDU(vect_command);
        if (rep.getSW() == 0x9000){
            System.out.println(" Ok, written !");
        }
        else
            System.out.println(" Error, couldn't write!(0x" + Integer.toHexString(rep.getSW())+")");
    }
    public static void CreateFile(int length, int num_enr,int n_of, int security_read, int security_write, int n1, int n2 ) throws CardException {
        byte[] vect_command;
        selectFile((byte) 0xFF, (byte) 0x04, 0x9000);
        vect_command = new byte[11];
        vect_command[0] = (byte) 0x80;
        vect_command[1] = (byte) 0xD2;
        vect_command[2] = (byte) n_of;
        vect_command[3] = 0x00;
        vect_command[4] = 0x06;
        vect_command[5] = (byte) length;  // 32 octets
        vect_command[6] = (byte) num_enr;  // 4 enregistrements
        vect_command[7] = (byte) security_read;
        vect_command[8] =  (byte) security_write; //1000
        vect_command[9] = (byte) n1;
        vect_command[10] =  (byte) n2;
        writeFile(vect_command);
        System.out.println(" file created");

    }
    public static void checkIc_Code() throws CardException {
        /**Fonction pour envoyer le code administrateur IC (Issuer Code)
         *  affin de se donner la permission de modifier le contenu des fichiers systèmes ;
         **/
        ResponseAPDU rep =submit_APDU(APDU_IC_CODE);
        if (rep.getSW() == 0x9000){
            System.out.println(" Ok, IC code verified !");
        }
        else
            System.out.println(" Error, IC code not verified! ( 0x"+Integer.toHexString(rep.getSW())+")");
    }
    static void clearCard() throws CardException {
        byte[] vect_command = new byte[4];
        vect_command[0] = (byte) 0x80;
        vect_command[1] = 0x30;
        ResponseAPDU rep =submit_APDU(vect_command);
        if (rep.getSW() == 0x9000){
            System.out.println(" Ok, card cleared ! \n All good Ready to GO!\n ");
        }
        else
            System.out.println(" Error, card not cleared! ( 0x"+Integer.toHexString(rep.getSW())+")");

    }
    private  static ResponseAPDU submit_APDU( byte[] APDU_CODE) throws CardException {
        CommandAPDU SubmitAPDU = new CommandAPDU(APDU_CODE);
        ResponseAPDU rep = SmartCardAcos.canal.transmit(SubmitAPDU);
        return  rep;
    }
    public void checkPincode() throws CardException {
        CommandAPDU SubmitAPDU = new CommandAPDU(APDU_PIN_CODE);
        ResponseAPDU rep = canal.transmit(SubmitAPDU);

        if (rep.getSW() == 0x9000){
            System.out.println(" Ok, PIN code verified !");
        }
        else
            System.out.println(" Error, PIN code not verified! ( 0x"+Integer.toHexString(rep.getSW())+")");
    }
    public void changePinCode(byte [] PIN_CODE) throws CardException {
        selectFile((byte) 0xFF,(byte) 0x03,0x9000);
        //write record (my name) in user file
        byte[] vect_command = new byte[13];
        vect_command[0] = (byte) 0x80;
        vect_command[1] = (byte)0xD2;
        vect_command[2] = 0x01;
        vect_command[3] = 0x00;
        vect_command[4] = 0x08;
        for (int i=0;i<8;i++){
            vect_command[5+i] = PIN_CODE[i];
        }
        writeFile(vect_command);
        CommandAPDU SubmitAPDU = new CommandAPDU(APDU_PIN_CODE);
        ResponseAPDU rep = canal.transmit(SubmitAPDU);

        if (rep.getSW() == 0x9000){
            System.out.println(" Ok, PIN code verified !");
        }
        else
            System.out.println(" Error, PIN code not verified! ( 0x"+Integer.toHexString(rep.getSW())+")");
    }
}
