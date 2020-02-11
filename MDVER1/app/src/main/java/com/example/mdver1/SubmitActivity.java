package com.example.mdver1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;


public class SubmitActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO = 0;
    private static String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    private String AUDIO_FILE_NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        setActionBar();
        recordAudio();

        String[] listItem = getResources().getStringArray(R.array.carIssue_array);
        String[] solutionItem = getResources().getStringArray(R.array.car_solution);

        ImageView imageView = (ImageView)findViewById(R.id.issueImage);
        TextView causeText = (TextView)findViewById(R.id.cause);
        TextView solutionText = (TextView)findViewById(R.id.solution);
        Button button = (Button)findViewById(R.id.exitButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int[] resultImage = new int[]{
                R.drawable.ball_joint_problem,
                R.drawable.bad_brake_pad,
                R.drawable.no_oil,
                R.drawable.failing_water_pump,
                R.drawable.hole
        };

        imageView.setImageResource(resultImage[2]);
        causeText.setText(listItem[2]);
        solutionText.setText(solutionItem[2]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    public void run() {
                        NetworkAsync networkTask = new NetworkAsync();
                        networkTask.execute();
                    }
                }).start();
                result();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
                Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // create random name and set the file name
    public void makeName() {
        long now = System.currentTimeMillis();
        int rnd = 0;
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            rnd += (int)(random.nextInt(10)) * Math.pow(10, i);
        }
        AUDIO_FILE_NAME = Long.toString(now) + Integer.toString(rnd) + ".wav";
        AUDIO_FILE_PATH += AUDIO_FILE_NAME;
        Log.i("file name", AUDIO_FILE_PATH);
        storeName();
    }

    // store name of wav file in mobile
    public void storeName() {
        SharedPreferences sharedPreferences_filename =  getSharedPreferences("wavfilename", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences_filename.edit();
        editor.putString("name", AUDIO_FILE_NAME);
        editor.commit();
    }

    public void recordAudio() {
        makeName();
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(AUDIO_FILE_PATH)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setRequestCode(REQUEST_RECORD_AUDIO)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_44100)
                .setAutoStart(false)
                .setKeepDisplayOn(true)
                // Start recording
                .record();
    }

    public void result(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                LinearLayout loadlayout = (LinearLayout)findViewById(R.id.loadlayout);
                LinearLayout submitlayout = (LinearLayout)findViewById(R.id.submitlayout);
                loadlayout.setVisibility(View.INVISIBLE);
                submitlayout.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }

    private void setActionBar() {
        CustomActionBar ca = new CustomActionBar(this, getSupportActionBar());
        ca.setActionBar();
    }

    public class NetworkAsync extends AsyncTask<Void, Void, JSONObject> {
        final static String TAG = "NetworkAsync";

        public NetworkAsync(){
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG,"onPreExecute()");
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            String boundary = "^-----^";
            String LINE_FEED = "\r\n";
            String charset = "UTF-8";
            OutputStream outputStream;
            PrintWriter writer;

            JSONObject result = null;

            File file = new File(AUDIO_FILE_PATH);
            try {
                URL url = new URL("http://52.14.78.174:5000/fileUpload");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary=" + boundary);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setConnectTimeout(15000);

                outputStream = connection.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

                /** Body에 데이터를 넣어줘야 할경우 없으면 Pass **/
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"데이터 키값\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append("데이터값").append(LINE_FEED);
                writer.flush();

                /** 파일 데이터를 넣는 부분**/
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append(LINE_FEED);
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(LINE_FEED);
                writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                FileInputStream inputStream = new FileInputStream(file);
                byte[] buffer = new byte[(int) file.length()];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();
                writer.append(LINE_FEED);
                writer.flush();

                writer.append("--" + boundary + "--").append(LINE_FEED);
                writer.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    ArrayList<String> result2 = new ArrayList<String>();
                    try {
                        result = new JSONObject(response.toString());
                        Log.i("정보", result+"");
                        Iterator i = result.keys();
                        Log.i("정보", result.keys()+"");

                        while(i.hasNext())
                        {
                            String b = i.next().toString();
                            result2.add(b);
                            Log.i("정보", b+"");

                        }

                        for(int j = 0; j<result2.size();j++) // 추출
                        {
                            result2.add(result.getString(result2.get(j)));
                            Log.i("정보2", result2+"");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    result = new JSONObject(response.toString());
                    Log.i("정보", result+"");
                }

            } catch (ConnectException e) {
                Log.e(TAG, "ConnectException");
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
    }
}
