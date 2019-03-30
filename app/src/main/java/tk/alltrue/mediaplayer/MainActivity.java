package tk.alltrue.mediaplayer;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnCompletionListener{

    private Button mPlayPauseButton;
    private Button mNextButton;
    private Button mStopButton;

    private TextView mStateTextView;
    private int mMediaPlayerState;

    private final int MP_STATE_NOTSTARTER = 0;
    private final int MP_STATE_PLAYING = 1;
    private final int MP_STATE_PAUSING = 2;

    private int[] mTracks = new int[2];
    private int mCurrentTrack = 0;
    private MediaPlayer mMediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTracks[0] = R.raw.volno;
        mTracks[1] = R.raw.gryaz;

        mStateTextView = findViewById(R.id.stateTextView);
        mPlayPauseButton = findViewById(R.id.buttonPausePlay);
        mStopButton = findViewById(R.id.buttonStop);
        mNextButton = findViewById(R.id.buttonNext);

        mPlayPauseButton.setOnClickListener(onPlayPauseClickListener);
        mNextButton.setOnClickListener(onNextClickListener);
        mStopButton.setOnClickListener(onStopClickListener);


        mMediaPlayer = MediaPlayer.create(getApplicationContext(), mTracks[mCurrentTrack]);
        mMediaPlayer.setOnCompletionListener(this);
    }

    Button.OnClickListener onPlayPauseClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (mMediaPlayerState) {
                case MP_STATE_NOTSTARTER:
                    mMediaPlayer.start();
                    mPlayPauseButton.setText(R.string.pause);
                    mStateTextView.setText(R.string.playing);
                    mMediaPlayerState = MP_STATE_PLAYING;
                    break;
                case MP_STATE_PLAYING:
                    mMediaPlayer.pause();
                    mPlayPauseButton.setText(R.string.play);
                    mStateTextView.setText(R.string.paused);
                    mMediaPlayerState = MP_STATE_PAUSING;
                    break;
                case MP_STATE_PAUSING:
                    mMediaPlayer.start();
                    mPlayPauseButton.setText(R.string.pause);
                    mStateTextView.setText(R.string.playing);
                    mMediaPlayerState = MP_STATE_PLAYING;
                    break;
            }
        }
    };

    Button.OnClickListener onNextClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMediaPlayer.release();
            mCurrentTrack++;
            mCurrentTrack = mCurrentTrack%mTracks.length;
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), mTracks[mCurrentTrack]);
            mMediaPlayer.start();
        }
    };

    Button.OnClickListener onStopClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            finish();
        }
    };


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mMediaPlayer.release();
        if (mCurrentTrack < mTracks.length) {
            mCurrentTrack++;
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), mTracks[mCurrentTrack]);
            mMediaPlayer.start();
        }
    }
}
