package com.pandev.currencyratebot.service.impl;

import com.pandev.currencyratebot.WrittenNumberToNumeralConverter;
import com.pandev.currencyratebot.service.SpeechToTextService;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

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

            return convertSpelledNumericalDigitsToNumber(convertSpelledOutNumbersToNumerals(result));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Convert exception";
    }

    @Override
    public String convertSpelledOutNumbersToNumerals(String text) {
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String trimmedWord = word.trim();
            try {
                int number = new WrittenNumberToNumeralConverter().parse(trimmedWord);
                result.append(number);
            } catch (NumberFormatException e) {
                result.append(trimmedWord);
            }
            result.append(" ");
        }

        return result.toString().trim();
    }

    @Override
    public String convertSpelledNumericalDigitsToNumber(String text) {
        String[] words = text.split(" ");
        ArrayList<String> digits = new ArrayList<>(Arrays.asList("trillion", "billion", "million", "thousand", "hundred", "dollars", "and", "cents"));
        BigDecimal result = BigDecimal.valueOf(0.00);
        BigDecimal digit = BigDecimal.valueOf(0.00);

        for (String word : words) {
            if(digits.contains(word)) {
                switch (word) {
                    case ("trillion"):
                        digit = digit.multiply(new BigDecimal("1000000000000"));
                        result = result.add(digit);
                        digit = new BigDecimal("0");
                        break;
                    case ("billion"):
                        digit = digit.multiply(new BigDecimal("1000000000"));
                        result = result.add(digit);
                        digit = new BigDecimal("0");
                        break;
                    case ("million"):
                        digit = digit.multiply(new BigDecimal("1000000"));
                        result = result.add(digit);
                        digit = new BigDecimal("0");
                        break;
                    case ("thousand"):
                        digit = digit.multiply(new BigDecimal("1000"));
                        result = result.add(digit);
                        digit = new BigDecimal("0");
                        break;
                    case ("hundred"):
                        digit = digit.multiply(new BigDecimal("100"));
                        break;
                    case ("dollars"):
                        result = result.add(digit);
                        digit = new BigDecimal("0");
                        break;
                    case ("cents"):
                        digit = digit.divide(new BigDecimal("100"));
                        result = result.add(digit);
                        break;
                    case ("and"):
                        break;
                    default:
                        digit = new BigDecimal("-1");
                }
            } else {
                digit = digit.add(new BigDecimal(word));
            }
        }

        return result + "$";
    }
}
