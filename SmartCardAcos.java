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

    public static void setApduIcCode(byte[] apduIcCode) {
        APDU_IC_CODE = apduIcCode;
    }

    public static void setApduPinCode(byte[] apduPinCode) {
        APDU_PIN_CODE = apduPinCode;
    }

    public void setLecteur(CardTerminal lecteur) {
        this.lecteur = lecteur;
    }
    public void setCanal(CardChannel canal) {
        this.canal = canal;
    }

    static  void selectFile(byte one, byte two, int SW) throws CardException {
        byte[] select_command = new byte[7];
        select_command[0] = (byte) 0x80;
        select_command[1] = (byte)0xA4;
        select_command[2] = 0x00;
        select_command[3] = 0x00;
        select_command[4] = 0x02;
        select_command[5] =  one;
        select_command[6] = two;
        ResponseAPDU rep =submit_APDU(select_command);
        if (rep.getSW() == SW){
            System.out.println(" Ok," + String.format("0x%02X", one)+String.format("%02X", two) + " Selected !");
        }
        else
            System.out.println(" Error, FF02 not Selected!(0x" + Integer.toHexString(rep.getSW())+")");
    }
    static  void writeFile(byte[] vect_command) throws CardException {
        ResponseAPDU rep =submit_APDU(vect_command);
        if (rep.getSW() == 0x9000){
            System.out.println(" Ok, written !");
        }
        else
            System.out.println(" Error, couldn't write!(0x" + Integer.toHexString(rep.getSW())+")");
    }

    static void checkIc_Code() throws CardException {
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
            System.out.println(" Ok, card cleared ! \nAll good Ready to GO! ");
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
