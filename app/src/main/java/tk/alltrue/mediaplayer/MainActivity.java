package tk.alltrue.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;

public class MainActivity extends AppCompatActivity {

    private Button mPlayPauseButton;
    private Button mNextButton;
    private Button mStopButton;
    private Button mOpenButton;

    private TextView mStateTextView;
    private String PATH_TO_FILE;
    private Uri PATH_URI;

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
        mPlayPauseButton = findViewById(R.id.buttonPausePlay);
        mStopButton = findViewById(R.id.buttonStop);
        mNextButton = findViewById(R.id.buttonNext);
        mOpenButton = findViewById(R.id.buttonOpen);

        mPlayPauseButton.setOnClickListener(onPlayPauseClickListener);
       // mNextButton.setOnClickListener(onNextClickListener);
        mStopButton.setOnClickListener(buttonQuitOnClickListener);
        mOpenButton.setOnClickListener(onOpenClickListener);
        //mMediaPlayer = MediaPlayer.create(getApplicationContext(), mTracks[mCurrentTrack]);
        //mMediaPlayer.setOnCompletionListener(this);
        mediaPlayer = new  MediaPlayer();
    }

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            System.out.println("getPath() uri: " + uri.toString());
            System.out.println("getPath() uri authority: " + uri.getAuthority());
            System.out.println("getPath() uri path: " + uri.getPath());

            // ExternalStorageProvider
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                System.out.println("getPath() docId: " + docId + ", split: " + split.length + ", type: " + type);

                // This is for checking Main Memory
                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1] + "/";
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                    // This is for checking SD Card
                } else {
                    return "storage" + "/" + docId.replace(":", "/");
                }

            }
        }
        return null;
    }
    private void initMediaPlayer()
    {
        PATH_TO_FILE = getPath(this, PATH_URI);

        try {

            FileInputStream fileInputStream = new FileInputStream(PATH_TO_FILE);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fileInputStream.getFD());
            mediaPlayer.prepare();
            Toast.makeText(this, PATH_TO_FILE, Toast.LENGTH_LONG).show();
            stateMediaPlayer = stateMP_NotStarter;
            mStateTextView.setText("- IDLE -");
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
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            stateMediaPlayer = stateMP_Error;
            //mStateTextView.setText("- ERROR!!! -");
        }
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
        }
       initMediaPlayer();
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
/*
    Button.OnClickListener onNextClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mediaPlayer.release();
            mCurrentTrack++;
            mCurrentTrack = mCurrentTrack%mTracks.length;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), mTracks[mCurrentTrack]);
            mediaPlayer.start();
        }
    };

    Button.OnClickListener onStopClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mediaPlayer.stop();
            mediaPlayer.release();
            finish();
        }
    };
*/
/*
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.release();
        if (mCurrentTrack < mTracks.length) {
            mCurrentTrack++;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), mTracks[mCurrentTrack]);
            mediaPlayer.start();
        }
    }
    */

    Button.OnClickListener buttonQuitOnClickListener
            = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            mediaPlayer.stop();
            mediaPlayer.release();
            finish();
        }
    };

}
