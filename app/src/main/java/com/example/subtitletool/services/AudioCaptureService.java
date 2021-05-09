package com.example.subtitletool.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.subtitletool.R;
import com.example.subtitletool.constants.Constants;
import com.example.subtitletool.thread.CustomThreadPool;
import com.example.subtitletool.utils.ContextUtils;
import com.example.subtitletool.utils.FileUtils;
import com.example.subtitletool.utils.PcmToWavUtils;
import com.example.subtitletool.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioCaptureService extends Service {

    private static final String TAG = AudioCaptureService.class.getSimpleName();

    private static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(Constants.SAMPLE_RATE_INHZ, Constants.CHANNEL_CONFIG, Constants.AUDIO_FORMAT);
    private static final CustomThreadPool threadPoolAudioCapture = new CustomThreadPool(Thread.MAX_PRIORITY);

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private AudioRecord audioRecord;

    private volatile boolean mIsAudioCaptureReady;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        createNotificationChannel();
        // use applicationContext to avoid memory leak on Android 10.
        // see: https://partnerissuetracker.corp.google.com/issues/139732252
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(Constants.AUDIO_CAPTURE_SERVICE_START)) {
            Notification notification = new NotificationCompat.Builder(ContextUtils.getContext(), Constants.AUDIO_CAPTURE_SERVICE_CHANNEL)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.audio_capturing))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            startForeground(Constants.AUDIO_CAPTURE_SERVICE_CHANNEL_ID, notification);
            mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, intent.getParcelableExtra(Constants.AUDIO_CAPTURE_SERVICE_EXTRA_RESULT_DATA));
            startAudioCapture();
            // 系统被杀死后将尝试重新创建服务
            return START_STICKY;
        } else {
            stopAudioCapture();
            stopForeground(true);
            stopSelf();
            // 系统被终止后将不会尝试重新创建服务
            return START_NOT_STICKY;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopAudioCapture();
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                Constants.AUDIO_CAPTURE_SERVICE_CHANNEL,
                Constants.AUDIO_CAPTURE_SERVICE_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private void startAudioCapture() {
        AudioPlaybackCaptureConfiguration config = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA) // TODO provide UI options for inclusion/exclusion
                .build();

        AudioFormat audioFormat = new AudioFormat.Builder()
                .setEncoding(Constants.AUDIO_FORMAT)
                .setSampleRate(Constants.SAMPLE_RATE_INHZ)
                .setChannelMask(Constants.CHANNEL_CONFIG)
                .build();

        audioRecord = new AudioRecord.Builder()
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(MIN_BUFFER_SIZE)
                .setAudioPlaybackCaptureConfig(config)
                .build();

        audioRecord.startRecording();
        startAudioCaptureTask();
    }

    private void startAudioCaptureTask() {
        threadPoolAudioCapture.execute(() -> {
            File outputFile = createAudioFile();
            Log.d(TAG, "Created file for capture target: " + outputFile.getAbsolutePath());
            mIsAudioCaptureReady = true;
            writeAudioToFile(outputFile);
        });
    }

    private File createAudioFile() {
        File audioCapturesDirectory = new File(FileUtils.APP_DIR, "/AudioCaptures");
        FileUtils.deleteDirectory(audioCapturesDirectory.getAbsolutePath());
        if (!audioCapturesDirectory.exists()) {
            audioCapturesDirectory.mkdirs();
        }
        String fileName = "AudioCapture.pcm";
        return new File(audioCapturesDirectory.getAbsolutePath() + "/" + fileName);
    }

    private void writeAudioToFile(File outputFile) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            byte[] data = new byte[MIN_BUFFER_SIZE];
            while (mIsAudioCaptureReady) {
                int read = audioRecord.read(data, 0, MIN_BUFFER_SIZE);
                // This loop should be as fast as possible to avoid artifacts in the captured audio
                // You can uncomment the following line to see the capture samples but
                // that will incur a performance hit due to logging I/O.
                // Log.v(LOG_TAG, "Audio samples captured: ${capturedAudioSamples.toList()}")
                // fileOutputStream.write(capturedAudioSamples, 0, BUFFER_SIZE_IN_BYTES);
                // 如果读取音频数据没有出现错误，就将数据写入到文件
                if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                    fileOutputStream.write(data);
                }
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Audio capture finished. File size is " + outputFile.length() + " bytes.");
    }

    private void stopAudioCapture() {
        mIsAudioCaptureReady = false;
        threadPoolAudioCapture.release();
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
        convertPcmToWav();
        ToastUtils.showShortSafe("Pcm to Wav converted");
    }

    private void convertPcmToWav() {
        File audioCapturesDirectory = new File(FileUtils.APP_DIR, "/AudioCaptures");
        if (!audioCapturesDirectory.exists()) {
            return;
        }
        PcmToWavUtils pcmToWavUtils = new PcmToWavUtils(Constants.SAMPLE_RATE_INHZ, Constants.CHANNEL_CONFIG, Constants.AUDIO_FORMAT);
        File pcmFile = new File(FileUtils.APP_DIR + "AudioCaptures/", "AudioCapture.pcm");
        File wavFile = new File(FileUtils.APP_DIR + "AudioCaptures/", "AudioCapture.wav");
        if (wavFile.exists()) {
            wavFile.delete();
        }
        pcmToWavUtils.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());
    }
}