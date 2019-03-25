package tk.alltrue.mediaplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    private Button mPlayPauseButton;
    private Button mStopButton;
    private TextView mStateTextView;
    private int mMediaPlayerState;
    private final int MP_STATE_NOTSTARTER = 0;
    private final int MP_STATE_PLAYING = 1;
    private final int MP_STATE_PAUSING = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaPlayer = MediaPlayer.create(this, R.raw.volno);
        mStateTextView = findViewById(R.id.stateTextView);
        mPlayPauseButton = findViewById(R.id.buttonPausePlay);
        mStopButton = findViewById(R.id.buttonStop);
        mPlayPauseButton.setOnClickListener(onPlayPauseClickListener);
        mStopButton.setOnClickListener(onStopClickListener);

    }
        Button.OnClickListener onPlayPauseClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mMediaPlayerState) {
                    case MP_STATE_NOTSTARTER:
                        mMediaPlayer.start();
                        mPlayPauseButton.setText("Pause");
                        mStateTextView.setText("- Playing -");
                        mMediaPlayerState = MP_STATE_PLAYING;
                        break;
                    case MP_STATE_PLAYING:
                        mMediaPlayer.pause();
                        mPlayPauseButton.setText("Playing");
                        mStateTextView.setText("- Paused -");
                        mMediaPlayerState = MP_STATE_PAUSING;
                        break;
                    case MP_STATE_PAUSING:
                        mMediaPlayer.start();
                        mPlayPauseButton.setText("Pause");
                        mStateTextView.setText("- Playing -");
                        mMediaPlayerState = MP_STATE_PLAYING;
                        break;
                }
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
}
