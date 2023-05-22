package com.pandev.currencyratebot.service;

import java.io.File;

public interface SpeechToTextService {

    String convertAudioToText(File audioFile);
}
