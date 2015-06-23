package dnetwork.minesystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import usbserial.driver.UsbSerialDriver;
import usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SerialConsoleActivity extends Activity {

    private static  UsbSerialDriver sDriver = null;
    private static  String app_folder;
    private String  show_text;

    private final   String TAG = SerialConsoleActivity.class.getSimpleName();
    private final   String END_WORD = "TOTAL";
    private final   ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private StringBuilder file_string;
    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SerialConsoleActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_serial_console);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        file_string = new StringBuilder();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sDriver != null) {
            try {
                sDriver.close();
            } catch (IOException e) {
                // Ignore.
            }
            sDriver = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, sDriver=" + sDriver);
        if (sDriver == null) {
        	Toast.makeText(getApplicationContext(), "No serial device.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                sDriver.open();
                sDriver.setParameters(9600, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
                //doStartShowAdsTask();
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                Toast.makeText(getApplicationContext(), "Error opening device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                try {
                    sDriver.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sDriver = null;
                return;
            }
            Toast.makeText(getApplicationContext(), "Serial device: " + sDriver.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sDriver != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sDriver, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        try {
            //doStopShowAdsTask();
            file_string.append(new String(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // this should never happen because "UTF-8" is hard-coded.
            throw new IllegalStateException(e);
        }

        show_text = file_string.toString();
        //Toast.makeText(getApplicationContext(), show_text, Toast.LENGTH_SHORT).show();
        if (show_text.indexOf(END_WORD) > 0) {
            SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//        			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

//        			basket = new Basket();
//                	basket.readTextReceive(show_text);
//                	closeImageAds();
                    showResult();
//                	doStartShowAdsTask();
                    // clear file_string
                    file_string.setLength(0);
                    file_string.trimToSize();
                }
            });
        	
        	/*
        	basket = new Basket();
        	basket.readTextReceive(show_text);
        	closeImageAds();
        	*/
        	showResult();
        	/*doStartShowAdsTask();
        	// clear file_string
        	file_string.setLength(0);
        	file_string.trimToSize();
        	*/
        }
    }
    
    // -----------------------
    public void showResult() {
//        try {
//
//        	TextView hdNo = (TextView) findViewById(R.id.txt_hdNo);
//            hdNo.setText(basket.No());
//
//            TextView hdType = (TextView) findViewById(R.id.txt_hdType);
//            hdType.setText(basket.Type());
//
//            TextView hdDate = (TextView) findViewById(R.id.txt_hdDate);
//            hdDate.setText(basket.Date());
//
//            TextView hdLabel = (TextView) findViewById(R.id.txt_hdLabel);
//            hdLabel.setText(basket.Label());
//
//            TextView hdOP = (TextView) findViewById(R.id.txt_hdOP);
//            hdOP.setText(basket.OP());
//
//            // --------------------- begin add currency row ---------------------
//            int iTotalPrice = 0;
//            int iTotalAmount = 0;
//            DecimalFormat formatter = new DecimalFormat("###,###,###,###");
//            if (basket.Currencies.size() > 0) {
//            	TableLayout tbDenomi = (TableLayout) findViewById(R.id.tb_denomi);
//
//            	// delete money table row
//                int all_row = tbDenomi.getChildCount();
//                if (all_row > 4) {
//                	for (int i=3; i < all_row-1; i++) {
//                   		tbDenomi.removeViewAt(3);
//                   		//Toast.makeText(getApplicationContext(), "TB " +all_row+" C "+basket.Currencies.size()+" Remove "+i, Toast.LENGTH_SHORT).show();
//                	}
//                }
//                LayoutInflater inflater = getLayoutInflater();
//                int i = 2;
//
//                //StringBuilder t_str = new StringBuilder();
//                for (Currency c : basket.Currencies) {
//                	View row = inflater.inflate(R.layout.layout_tb_row, null, false);
//                    ImageView image = (ImageView) row.findViewById(R.id.img_banknote);
//
//                    // get image in sdcard first, then from asset if don't have any, show no image.
//                    Bitmap img_bitmap = null;
//                    String imgName = basket.Label() + "/" + basket.Label() + c.Denomi() + ".png";
//                    File imgFile = new File(app_folder + "/" + imgName);
//                    if(imgFile.exists()) {
//                        img_bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    } else {
//                    	try {
//                    		InputStream input = getAssets().open(imgName);
//                    		img_bitmap = BitmapFactory.decodeStream(input);
//                    	} catch(IOException ex) {
//                    		InputStream input = getAssets().open("no_image.png");
//                    		img_bitmap = BitmapFactory.decodeStream(input);
//                    	}
//                    }
//                    double scale = 0.7;
//                    int w = (int)(205 * scale);
//                    int h = (int)(100 * scale);
//                    img_bitmap = Bitmap.createScaledBitmap(img_bitmap, w, h, true);
//                    image.setLayoutParams(new TableRow.LayoutParams(w+30, h+10));
//                    image.setImageBitmap(img_bitmap);
//
//                    AutoResizeTextView denomi = (AutoResizeTextView) row.findViewById(R.id.txt_denomi);
//                    denomi.setText(c.Denomi());
//                    AutoResizeTextView pcs = (AutoResizeTextView) row.findViewById(R.id.txt_piece);
//                    pcs.setText(c.Piece());
//                    iTotalPrice += Integer.parseInt(c.Piece());
//                    AutoResizeTextView amount = (AutoResizeTextView) row.findViewById(R.id.txt_amount);
//                    int _amount = Integer.parseInt(c.Denomi()) * Integer.parseInt(c.Piece());
//                    amount.setText( formatter.format(_amount) );
//                    iTotalAmount += _amount;
//                    row.setLayoutParams( new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, h+6) );
//                    tbDenomi.addView( row, ++i );
//
//                } // end currency loop
//                //Toast.makeText(getApplicationContext(), t_str.toString(), Toast.LENGTH_SHORT).show();
//            }
//            // --------------------- end add currency row ---------------------
//
//            TableRow trow = (TableRow)findViewById(R.id.total_row);
//            trow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 76));
//            AutoResizeTextView totalPiece = (AutoResizeTextView) findViewById(R.id.txt_totalPiece);
//            totalPiece.setText( formatter.format(iTotalPrice) );
//            AutoResizeTextView totalAmount = (AutoResizeTextView) findViewById(R.id.txt_totalAmount);
//            totalAmount.setText( formatter.format(iTotalAmount) );
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    

    static void show(Context context, UsbSerialDriver driver, String file_path) {
        sDriver = driver;
        app_folder = file_path;
        final Intent intent = new Intent(context, SerialConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}
