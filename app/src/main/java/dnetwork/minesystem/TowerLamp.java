package dnetwork.minesystem;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

/**
 * Created by thamrong on 6/23/15 AD.
 */
public class TowerLamp {
    private String LampStatus   = "";
    private String LampErrorMsg = "";
    public List<Currency> Currencies;

    private final String START_WORD = "SEQ-NO.";
    private final String END_WORD   = "TOTAL";
    private final String DENOMI_WORD = "DENOMI.";

    public String getLampStatus() {
        return LampStatus;
    }
    public String getLampErrorMsg() {
        return LampErrorMsg;
    }

    public void setLampStatus(String lampStatus) {
        LampStatus = lampStatus;
    }
    public void setLampErrorMsg(String lampErrorMsg) {
        LampErrorMsg = lampErrorMsg;
    }

    public void readTextReceive(String show_text) {

        Log.d("LOG =======: ", "log start");


        // cut white space in input text
        String compress_str = TextUtils.join(" ", show_text.split("\\s+"));
        // ================== get data
        String final_str = "";
        String[] temp = compress_str.split(" ");
        int start_index = -1;
        for (int i=0; i<temp.length; i++) {
            if (temp[i].indexOf(START_WORD) > -1) {
                start_index = i;
                break;
            }
        }
        int end_index = Arrays.asList(temp).indexOf(END_WORD);
        int denomi_index = Arrays.asList(temp).indexOf(DENOMI_WORD);


        Log.d("LOG =======: ", "log end");

    }
}
