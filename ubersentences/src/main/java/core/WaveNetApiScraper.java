package core;


import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import models.Sentence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static config.Config.*;

public class WaveNetApiScraper {

    private final VoiceSelectionParams voice;
    private final AudioConfig audioConfig;
    private final TextToSpeechSettings textToSpeechSettings;

    WaveNetApiScraper() {
        this.voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("ja-JP")
                .setName("ja-JP-Wavenet-C")
                .setSsmlGender(SsmlVoiceGender.MALE)
                .build();
        this.audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();
        try {
            final ServiceAccountCredentials credentials = ServiceAccountCredentials
                    .fromStream(Files.newInputStream(WAVENET_DATA));
            this.textToSpeechSettings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't load credentials.", e);
        }
    }

    public void create(final Sentence sentence) {
        try (final TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(this.textToSpeechSettings)) {
            final SynthesisInput input = SynthesisInput.newBuilder().setText(sentence.getSentenceForTts()).build();
            final SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, this.voice, this.audioConfig);
            final ByteString audioContents = response.getAudioContent();
            this.writeToFile(audioContents, sentence.getMp3File());
        } catch (final Exception e) {
            throw new IllegalStateException("Couldn't create mp3.", e);
        }
    }

    private void writeToFile(final ByteString audioContents, final String mp3fileName) {
        try (final OutputStream out = new FileOutputStream(MP3_FOLDER.resolve(DECK).resolve(mp3fileName).toFile())) {
            out.write(audioContents.toByteArray());
        } catch (final Exception e) {
            throw new IllegalStateException("Couldn't write OutputStream to file.", e);
        }
    }

    public static String createFileName(final String reading) {
        return reading + ".mp3";
    }

    // ------------------------------------------------------------------------------------------ //
    // PRIVATE
    // ------------------------------------------------------------------------------------------ //


}
