package com.pandev.currencyratebot.service.impl;

import com.pandev.currencyratebot.WrittenNumberToNumeralConverter;
import com.pandev.currencyratebot.service.SpeechToTextService;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
public class SpeechToTextServiceImpl implements SpeechToTextService {

    @Override
    public String convertAudioToText(File audioFile) {
        Configuration configuration = new Configuration();

        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        try (InputStream audioStream = new FileInputStream(audioFile)) {
            StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
            recognizer.startRecognition(audioStream);
            String result = recognizer.getResult().getHypothesis();
            recognizer.stopRecognition();

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Convert exception";
    }
}
