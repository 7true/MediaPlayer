package tk.alltrue.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener{

    private Button mPlayPauseButton;
    private Button mNextButton;
    private Button mStopButton;
    private Button mOpenButton;

    private TextView mStateTextView;
    private TextView mCurrentTrackTextView;
    private String PATH_TO_FILE;
    private Uri PATH_URI;
    private ArrayList<String> listmp3 = new ArrayList<String>();
    private ArrayList<String> listmp3Names = new ArrayList<String>();

    private final int stateMP_Error = 0;
    private final int stateMP_NotStarter = 1;
    private final int stateMP_Playing = 2;
    private final int stateMP_Pausing = 3;

    private final int INTENT_FOLDER_CODE = 1001;

    private int[] mTracks = new int[2];
    private int mCurrentTrack = 0;
    private int stateMediaPlayer;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTracks[0] = R.raw.volno;
        mTracks[1] = R.raw.gryaz;

        mStateTextView = findViewById(R.id.stateTextView);
        mCurrentTrackTextView = findViewById(R.id.textViewCurrent);
        mPlayPauseButton = findViewById(R.id.buttonPausePlay);
        mStopButton = findViewById(R.id.buttonStop);
        mNextButton = findViewById(R.id.buttonNext);
        mOpenButton = findViewById(R.id.buttonOpen);
        String path = Environment.getExternalStorageDirectory().toString()+"/Download";
        loadmp3(path);
        mPlayPauseButton.setOnClickListener(onPlayPauseClickListener);
        mNextButton.setOnClickListener(onNextClickListener);
        mStopButton.setOnClickListener(onStopClickListener);
        mOpenButton.setOnClickListener(onOpenClickListener);

        mediaPlayer = new  MediaPlayer();
        mCurrentTrack = -1;
        playNext();
        mediaPlayer.pause();
        mStateTextView.setText("- IDLE -");
        stateMediaPlayer = stateMP_NotStarter;
        mediaPlayer.setOnCompletionListener(this);

    }

    private void loadmp3(String pathMusic) {
        Log.d("Files", "Path: " + pathMusic);
        File directory = new File(pathMusic);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].getAbsolutePath().endsWith("mp3")) {
                Log.d("Files MP3", "FileName:" + files[i].getName());
                listmp3.add(files[i].getAbsolutePath());
                listmp3Names.add(files[i].getName());
            }
        }
    }


    private void initMediaPlayer()
    {

        PATH_TO_FILE = PathUtil.getFileName(PATH_URI,this);

        try {
            //FileInputStream fileInputStream = new FileInputStream(PATH_TO_FILE);
            //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //mediaPlayer.setDataSource(fileInputStream.getFD());
            //mediaPlayer.prepare();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), PATH_URI);
            Toast.makeText(this, PATH_TO_FILE, Toast.LENGTH_LONG).show();
            stateMediaPlayer = stateMP_NotStarter;
            mStateTextView.setText("- IDLE -");
            //PATH_TO_FILE = PATH_TO_FILE.substring(0, PATH_TO_FILE.length() - 1);
            //String fileName = PATH_TO_FILE.substring(PATH_TO_FILE.lastIndexOf("/")+1);
            mCurrentTrack = listmp3Names.indexOf(PATH_TO_FILE);
            mCurrentTrackTextView.setText(PATH_TO_FILE);
        }
        catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            stateMediaPlayer = stateMP_Error;
            mStateTextView.setText("- ERROR!!! -");
        }
        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            stateMediaPlayer = stateMP_Error;
            mStateTextView.setText("- ERROR!!! -");
        }
//        catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
//            stateMediaPlayer = stateMP_Error;
//            //mStateTextView.setText("- ERROR!!! -");
//        }
    }

    Button.OnClickListener onOpenClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Audio"), INTENT_FOLDER_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_FOLDER_CODE) {
                Uri audioFileUri = data.getData();
                PATH_URI = audioFileUri;
                PATH_TO_FILE = audioFileUri.getPath();
                mStateTextView.setText(PATH_TO_FILE);
            }
            mediaPlayer.release();
            initMediaPlayer();
        }
    }

    Button.OnClickListener onPlayPauseClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch(stateMediaPlayer){
                case stateMP_Error:
                    break;
                case stateMP_NotStarter:
                    mediaPlayer.start();
                    mPlayPauseButton.setText("Pause");
                    mStateTextView.setText("- PLAYING -");
                    stateMediaPlayer = stateMP_Playing;
                    break;
                case stateMP_Playing:
                    mediaPlayer.pause();
                    mPlayPauseButton.setText("Play");
                    mStateTextView.setText("- PAUSING -");
                    stateMediaPlayer = stateMP_Pausing;
                    break;
                case stateMP_Pausing:
                    mediaPlayer.start();
                    mPlayPauseButton.setText("Pause");
                    mStateTextView.setText("- PLAYING -");
                    stateMediaPlayer = stateMP_Playing;
                    break;
            }
        }
    };

    Button.OnClickListener onNextClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            playNext();
        }
    };

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mCurrentTrack < mTracks.length) {
            playNext();
        }
    }


    Button.OnClickListener onStopClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mediaPlayer.stop();
            mediaPlayer.release();
            finish();
        }
    };

    private void playNext() {
        mediaPlayer.release();
        mCurrentTrack++;
        mCurrentTrack = mCurrentTrack%listmp3.size();
        PATH_TO_FILE = listmp3.get(mCurrentTrack);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(PATH_TO_FILE));
        mCurrentTrackTextView.setText(PATH_TO_FILE.substring(PATH_TO_FILE.lastIndexOf("/")+1));
        mediaPlayer.start();
        stateMediaPlayer = stateMP_Playing;
        mStateTextView.setText("- PLAYING -");
    }

}
