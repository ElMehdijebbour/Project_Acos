package projet_sesnum;

import javax.smartcardio.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static projet_sesnum.Smart_main.check_card;
import static projet_sesnum.Smart_main.lecteur;

public class Read {
    private static byte [] APDU_IC_CODE = {(byte) 0x80,0x20,0x07,0x00, 0x08, 0x41, 0x43, 0x4F, 0x53, 0x54, 0x45, 0x53, 0x54};
    private static byte [] APDU_PIN_CODE = {(byte) 0x80,0x20,0x06,0x00, 0x08,0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
    private  static  int SW_system_files=  0x9000;
    private  static  int SW_user_files=  0x9100;
    public static void main(String[] args) throws CardException, NoSuchAlgorithmException, IOException {
        System.out.println("Phase 1 starting now");
        CardTerminal lecteur = lecteur();
        Card card = check_card(lecteur);
        CardChannel canal = card.getBasicChannel();
        SmartCardAcos smartCard = new SmartCardAcos(lecteur,card,canal,APDU_IC_CODE);
        String file_AA12 =Linker.getPublicKey();
        smartCard.setApduIcCode(APDU_IC_CODE);
        smartCard.checkIc_Code();
        smartCard.selectFile((byte) 0xAA, (byte) 0x12,SW_user_files);
        smartCard.setApduPinCode(APDU_PIN_CODE);
        smartCard.checkPincode();
        byte[] read_record ={(byte) 0x80 , (byte) 0xB2 , (byte) 0x00 ,
                0x00 , (byte) file_AA12.length() };
        CommandAPDU Read_APDU = new CommandAPDU ( read_record );
        ResponseAPDU rep = canal . transmit ( Read_APDU ) ;
        if ( rep . getSW () == 0x9000 )
        {
            System . out . print (" Ok read file AA12 "+":\n");
            System . out . println (new String(rep.getData()));
        }
        String [] file_AA10 = new String [4];
        file_AA10 [0]= " M";
        file_AA10 [1]= " JebbourDabachFigdal";
        file_AA10 [2]= " ID200130";
        file_AA10 [3]= " 24/4/2021";
        smartCard.selectFile((byte) 0xAA, (byte) 0x10,SW_user_files);
        for (int i =0; i <4; i ++) {
            read_record = new byte[]{(byte) 0x80, (byte) 0xB2, (byte) i,
                    0x00, (byte) file_AA10[i].length()};
            Read_APDU = new CommandAPDU(read_record);
            rep = canal.transmit(Read_APDU);
            if ( rep . getSW () == 0x9000 )
            {
                System . out . print (" Ok read file AA10 ,"+":");
                System . out . println (new String ( rep . getData () ));
            }
        }
    }
}
