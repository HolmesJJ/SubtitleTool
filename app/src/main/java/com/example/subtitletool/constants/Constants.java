package com.example.subtitletool.constants;

import android.media.AudioFormat;

public class Constants {

    public static final int PERMISSION_REQUEST_CODE = 101;
    public static final int MEDIA_PROJECTION_REQUEST_CODE = 102;
    public static final int MAIN_SERVICE_CHANNEL_ID = 103;

    public static final String MAIN_SERVICE_CHANNEL = "MainServiceChannel";
    public static final String MAIN_SERVICE_START = "MainServiceStart";
    public static final String MAIN_SERVICE_STOP = "MainServiceStop";
    public static final String MAIN_SERVICE_EXTRA_RESULT_DATA = "MainServiceExtraResultData";

    // 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用
    public static final int SAMPLE_RATE_INHZ = 44100;

    // 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    // 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    public Constants() {
    }
}
