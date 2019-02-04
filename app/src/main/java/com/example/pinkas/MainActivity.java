package com.example.pinkas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.internal.widget.AdapterViewCompat.OnItemSelectedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import thep.paillier.*;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;

public class MainActivity extends ActionBarActivity implements OnItemSelectedListener {

    private final String FILENAME = "discovery_times.txt";
    String fullMessage = "";
    private RelativeLayout parent = null;
    private Spinner key_spinner = null;
    private Spinner coefficient_spinner = null;
    private Spinner location_spinner = null;
    private TextView clientTitle = null;
    private int[] Freemont={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32};
    private int[] Hayward={33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64};
    int size = 4;
    int keySize = 64;
    int rootsNum = 0;
    int totalBits = 128;
    TextView textResponse=null;//yet to use to print response received from the server Bob
    TextView responseTitle=null;//yet to use to print response received from the server Bob

    int location1=0;
    int location2=0;
    String message = "";

    private Timer t;
    private int TimeCounter = 0;

    private PrivateKey privkey;
    private String resultText = "";
    private String serText = "";

    // Fetch the corresponding public key
    public PublicKey pubkey;

    EncryptedPolynomial encryptedList;


    private ArrayList<BigInteger> arrayOfRoots = null;
    private BigInteger[] arrayOfCoefficients;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msg = (TextView) findViewById(R.id.msg);

        parent = (RelativeLayout) findViewById(R.id.relLayout);
        setupKeySpinner();
        setupCoefficientSpinner();
        setLocationSpinner();

        //A TextView can also be created using activity.java file.
        clientTitle = (TextView) findViewById(R.id.clientTitle);

        final Button button = (Button) findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startProcess();
            }
        });
    }

    private void setupCoefficientSpinner() {
        coefficient_spinner = (Spinner) findViewById(R.id.coefficient_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> coefficient_adapter = ArrayAdapter.createFromResource(this,
                R.array.coefficient_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        coefficient_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        coefficient_spinner.setAdapter(coefficient_adapter);

        coefficient_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //stuff here to handle item selection
                //showToast("Coefficient Spinner: position=" + position + ", id= " + id + ", value=" + parent.getItemAtPosition(position));
                if (position == 0) {
                    size = 128;
                } else if (position == 1) {
                    size = 64;
                } else if (position == 2) {
                    size = 32;
                } else if (position == 3) {
                    size = 16;
                } else if (position == 4) {
                    size = 4;
                }
                showToast("Select root size = " + size);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showToast("Nothing selected from coefficients");
            }
        });
    }


    private void setLocationSpinner() {
        location_spinner = (Spinner) findViewById(R.id.location_spinner);
        ArrayAdapter<CharSequence> location_adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        location_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location_spinner.setAdapter(location_adapter);
        location_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //stuff here to handle item selection
                //showToast("Coefficient Spinner: position=" + position + ", id= " + id + ", value=" + parent.getItemAtPosition(position));
                if (position == 0) {
                    location1 = 1;
                }
                else if (position == 1) {
                    location2=1;
                }

                TextView mytext=(TextView) view;
                showToast("Location = " + mytext);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showToast("Nothing selected from keys");
            }
        });
    }
    private void setupKeySpinner() {
        key_spinner = (Spinner) findViewById(R.id.key_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> key_adapter = ArrayAdapter.createFromResource(this,
                R.array.key_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        key_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        key_spinner.setAdapter(key_adapter);
        key_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //stuff here to handle item selection
                //showToast("Coefficient Spinner: position=" + position + ", id= " + id + ", value=" + parent.getItemAtPosition(position));
                if (position == 0) {
                    keySize = 128;
                }
                else if (position == 1) {
                    keySize = 256;
                }
                else if (position == 2) {
                    keySize = 512;
                }
                showToast("Key size = " + keySize);
                privkey = new PrivateKey(keySize);

                // Fetch the corresponding public key
                pubkey = privkey.getPublicKey();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showToast("Nothing selected from keys");
            }
        });
    }

    private void startProcess() {
        ArrayList<BigInteger> tempList = generateRoots(size);
        arrayOfRoots = tempList;
        BigInteger []c = generateCoefficients(tempList);
        for (int i = 0; i < c.length; i++) {
            System.out.println("Coefficient no " + i + " is " + c[i]);
        }
        arrayOfCoefficients = c;
        //ArrayList<BigInteger> elist = encryptCoefficients(c);
        encryptedList = getPolynomial(c);
        for (int i = 0; i < encryptedList.getCoefficients().length; i++) {
            System.out.println("Encrypted Coefficient no " + i + " is " + encryptedList.getCoefficients()[i]);
        }
        communicate();
    }

    private void communicate() {
        startTimer();
        setupText();
        EncryptedInteger []enci = encryptedList.getCoefficients();
        String list = "";
        for(int i = 0; i < enci.length; i++) {
            list = list + enci[i].getCipherVal().toString();
            if (i < enci.length - 1) {
                list = list + "-";
            }
        }
        list = list + "/" + pubkey.getN().toString() + "-" + String.valueOf(pubkey.getBits()) + "-" + String.valueOf(size);
        MyClientTask myClientTask = new MyClientTask("127.0.0.1", 8080, list, encryptedList);
        myClientTask.execute();
//        ClientCommunicationTask task = new ClientCommunicationTask(encryptedList, size, keySize, pubkey);
//        task.execute();
    }

    private ArrayList<BigInteger> generateRoots(int rootSize) {


        ArrayList<BigInteger> list = new ArrayList<BigInteger>();
        if(size == 128) {
            rootsNum = 2;
        }
        else if (size == 64) {
            rootsNum = 4;
        }
        else if (size == 32) {
            rootsNum = 8;
        }
        else if (size == 16) {
            rootsNum = 16;
        }
        else if (size == 4) {
            rootsNum = 32;
        }

        if(location1==1)
        {
            for (int i = 0; i < rootsNum; i++) {

                BigInteger p = new BigInteger(Integer.toString(Freemont[i]));
                list.add(p);
            }
        }
        if(location2==1)
        {
            for (int i = 0; i < rootsNum; i++) {

                BigInteger p = new BigInteger(Integer.toString(Hayward[i]));
                list.add(p);
            }
        }
        return list;
    }

    private BigInteger[] generateCoefficients(ArrayList<BigInteger> list) {

        int n = list.size();
        BigInteger []c = new BigInteger[n+1];
        c[0] = new BigInteger(String.valueOf(1));
        for (int i = 1; i < c.length; i++) {
            c[i] = new BigInteger(String.valueOf(0));
        }

        for (int j = 0; j < n; j++) {
            for (int i = j; i >= 0; i--) {
                c[i+1] = c[i+1].subtract(list.get(j).multiply(c[i]));
            }
        }

        for(int i = 0; i < c.length / 2; i++)
        {
            BigInteger temp = c[i];
            c[i] = c[c.length - i - 1];
            c[c.length - i - 1] = temp;
        }

        return c;
    }

    private EncryptedPolynomial getPolynomial(BigInteger []c) {
        EncryptedPolynomial poly = null;
        try {
            poly = new EncryptedPolynomial(c, pubkey);
        } catch (BigIntegerClassNotValid bigIntegerClassNotValid) {
            bigIntegerClassNotValid.printStackTrace();
        }
        return poly;
    }

    void responseReceived(String response) {

        String[] d = response.split("/");
        String sumStr = d[0];
        String cofStr = d[1];

        String []data = sumStr.split("-");
        BigInteger zero = new BigInteger(String.valueOf("0"));
        boolean friendFound = true;
        message=response;

        for (int i = 0; i < data.length; i++) {

            BigInteger decSol = decrypt(new BigInteger(data[i]));
            System.out.println("RESPONSE RECEIVED");
            System.out.println(data[i]);

            System.out.println("DECRYPTED SUM = " + decSol);
            if (decSol.compareTo(zero) > 0 || decSol.compareTo(zero) < 0) {
                friendFound = false;
                break;
            }
        }

        if (friendFound) {
            resultText = "Server (Bob) found in " + String.valueOf(TimeCounter) + "ms";
            serText = "Server (Bob) is near!";
        }
        else {
            resultText = "Server (Bob) not found in " + String.valueOf(TimeCounter) + "ms";
            serText = "Server (Bob) is not near!";
        }
        writeToFile(String.valueOf(TimeCounter) + "-");
        stopTimer();


        String []data1 = cofStr.split("-");
        String cofs = "Encrypted Polynomial from Server (Bob) is : " + "\n";
        for (int i = 0; i < data1.length; i++) {
            cofs += data1[i];
            if (i < data1.length - 1) {
                cofs += ", ";
            }
        }

        String sums = "Evaluated response from Server (Bob) is : " + "\n";
        for (int i = 0; i < data.length; i++) {
            sums += data[i];
            if (i < data.length - 1) {
                sums += ", ";
            }
        }
        fullMessage += cofs + "\n";
        fullMessage += sums + "\n";
        fullMessage += serText + "\n";
        fullMessage += "=======================" + "\n";
        updateScrollView();
    }

    void responseReceived(ArrayList<EncryptedInteger> response) {

        BigInteger zero = new BigInteger(String.valueOf("0"));
        boolean friendFound = true;

        for (int i = 0; i < response.size(); i++) {
            BigInteger decSol = null;
            try {

                decSol = response.get(i).decrypt(privkey);
            } catch (BigIntegerClassNotValid bigIntegerClassNotValid) {
                bigIntegerClassNotValid.printStackTrace();
            }
            System.out.println(decSol);
            if (decSol.compareTo(zero) > 0 || decSol.compareTo(zero) < 0) {
                friendFound = false;
                break;
            }
        }

        String text = "";
        String serText = "";
        if (friendFound) {
            text = "Server (Bob) found in " + String.valueOf(TimeCounter) + "ms";
            serText = "Server (Bob) is near!";
        }
        else {
            text = "Server (Bob) not found in " + String.valueOf(TimeCounter) + "ms";
            serText = "Server (Bob) is not near!";
        }
        writeToFile(String.valueOf(TimeCounter) + "-");
        showMessageBox(text);
        stopTimer();
    }

    private void startTimer() {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    public void run() {
                        TimeCounter++;
                    }
                });

            }
        }, 0, 1); // first parameter means start from 0 sec (1 sec if 1000), and the second parameter is to loop each 1ms (1000 for 1 sec)
    }

    private void stopTimer() {
        t.cancel();//stopping the timer when ready to stop.
//        String timerString = "The time taken is "+ String.valueOf(TimeCounter) + "ms";
//        Log.v("TEST", timerString);
//        showToast(timerString);
        TimeCounter = 0;
    }


    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String msgToServer;
        EncryptedPolynomial poly;

        MyClientTask(String addr, int port, String msgTo, EncryptedPolynomial p) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
            poly = p;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if(msgToServer != null){
                    dataOutputStream.writeUTF(msgToServer);
                }

                response = dataInputStream.readUTF();
                responseReceived(response);


            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // textResponse.setText(response);
            //responseReceived(response);

            showMessageBox(resultText);
            super.onPostExecute(result);
        }

    }

    private class ClientCommunicationTask extends AsyncTask<Void, Void, ArrayList<EncryptedInteger>> {

        EncryptedPolynomial coefficientList;
        int roots;
        int kSize;
        PublicKey key;

        public ClientCommunicationTask(EncryptedPolynomial poly, int r, int ks, PublicKey k){
            this.coefficientList = poly;
            this.roots = r;
            this.kSize = ks;
            this.key = k;
        }

        @Override
        protected ArrayList doInBackground(Void... nums) {

            ArrayList<BigInteger> myRoots = generateRoots(roots);
            ArrayList<EncryptedInteger> response = new ArrayList<EncryptedInteger>();

            for (int j = 0; j < myRoots.size(); j++) {
                try {
                    BigInteger b = new BigInteger(String.valueOf(myRoots.get(j)));
                    EncryptedPolynomial tempP = new EncryptedPolynomial(this.coefficientList);
                    EncryptedInteger sum = tempP.evaluate(b);
                    response.add(sum);
                } catch (BigIntegerClassNotValid bigIntegerClassNotValid) {
                    bigIntegerClassNotValid.printStackTrace();
                } catch (PublicKeysNotEqualException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(ArrayList<EncryptedInteger> result) {
            Log.v("TEST", "OnPostExecute");
            responseReceived(result);
        }
    }

    BigInteger decrypt(BigInteger num) {
        BigInteger plainval;
        BigInteger c = num;
        plainval = c.modPow(privkey.getLambda(), privkey.getPublicKey().getNSquared());
        plainval = plainval.subtract(BigInteger.ONE);
        plainval = plainval.divide(privkey.getPublicKey().getN());
        plainval = plainval.multiply(privkey.getMu());
        plainval = plainval.mod(privkey.getPublicKey().getN());

        return plainval;
    }


    private void setupText() {
        fullMessage += "Location tags of Client (Alice) are :" + "\n";
        String roots = "";
        for (int i = 0; i < arrayOfRoots.size(); i++) {
            roots = roots + arrayOfRoots.get(i);
            if (i < arrayOfRoots.size() - 1) {
                roots = roots + ", ";
            }
        }
        fullMessage += roots + "\n";
        fullMessage += "Polynomial generated with roots is : " + "\n";

        String poly = "";
        for (int i = arrayOfCoefficients.length - 1; i >= 0; i--) {
            poly = poly + arrayOfCoefficients[i];
            if (i > 0) {
                poly = poly + ", ";
            }
        }
        fullMessage += poly + "\n";
        fullMessage += "Encrypted Values : " + "\n";

        String enc = "";
        String r = "";
        EncryptedInteger []t =  encryptedList.getCoefficients();
        for (int i = t.length - 1; i >= 0; i--) {
            enc = enc + t[i].getCipherVal().toString();
            r = r + t[i].randomNum;
            if (i > 0) {
                enc = enc + ", ";
                r = r + ", ";
            }
        }
        fullMessage += enc + "\n";
        fullMessage += "Generated random numbers used for encryption : " + "\n";
        fullMessage += r + "\n";

        fullMessage += "Looking for Server..." + "\n";
        updateScrollView();

    }

    private void updateScrollView() {
        runOnUiThread(new Runnable() {
            public void run() {
                msg.setText(fullMessage);
            }
        });
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });

    }
    private void showMessageBox(final String text) {

        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(text);
        dlgAlert.setTitle("Pinkas Test");
        dlgAlert.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                communicate();
            }
        });
        dlgAlert.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                System.out.println(readFromFile());
            }
        });

        dlgAlert.create();
        runOnUiThread(new Runnable() {
            public void run() {
                dlgAlert.show();
            }
        });
    }

    private void writeToFile(String data) {

        File file = new File(this.getFilesDir() + "/" + FILENAME);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            // Writes bytes from the specified byte array to this file output stream
            fos.write(data.getBytes());

//            String line = System.getProperty("-");
//            OutputStreamWriter osw = new OutputStreamWriter(fos);
//            osw.append(line); // this will add new line ;
//            osw.flush();
//            osw.close();

        }
        catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        }
        catch (IOException ioe) {
            System.out.println("Exception while writing file " + ioe);
        }
        finally {
            // close the streams using close method
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }
    }

    public String readFromFile() {
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fIn = openFileInput (FILENAME) ;
            InputStreamReader isr = new InputStreamReader ( fIn ) ;
            BufferedReader buffreader = new BufferedReader ( isr ) ;

            String readString = buffreader.readLine ( ) ;
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine ( ) ;
            }

            isr.close ( ) ;
        } catch ( IOException ioe ) {
            ioe.printStackTrace ( ) ;
        }
        return datax.toString();
    }

    @Override
    public void onItemSelected(AdapterViewCompat<?> arg0, View arg1, int arg2,
                               long arg3) {
//		// TODO Auto-generated method stub
//        showToast("Coefficient Spinner: position=" + arg2 + ", id= " + arg3 + ", value=" + arg0.getItemAtPosition(arg2));
//		switch(arg0.getId()){
//        case R.id.coefficient_spinner :
//            showToast("Coefficient Spinner: position=" + arg2 + ", id= " + arg3 + ", value=" + arg0.getItemAtPosition(arg2));
//              break;
//        case R.id.key_spinner :
//            showToast("Key Spinner: position=" + arg2 + ", id= " + arg3 + ", value=" + arg0.getItemAtPosition(arg2));
//        break;
//       }

    }

    @Override
    public void onNothingSelected(AdapterViewCompat<?> arg0) {
//        showToast("You selected nothing");
    }
}
