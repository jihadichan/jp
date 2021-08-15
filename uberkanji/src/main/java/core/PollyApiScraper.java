package core;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.*;
import models.PollyTtsData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static core.Config.AWS_API_KEYS;
import static core.Config.VOICE_ID;

/**
 * SOURCE: https://docs.aws.amazon.com/polly/latest/dg/examples-java.html
 */
public class PollyApiScraper {

    private final AmazonPollyClient pollyClient;
    private final Voice voice;

    PollyApiScraper() {
        // create an Amazon Polly client in a specific region
        final String[] keys = this.loadCredentials();
        this.pollyClient = new AmazonPollyClient(new BasicAWSCredentials(keys[0], keys[1]),
                new ClientConfiguration());
        this.pollyClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
        // Create describe voices request.
        final DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
        describeVoicesRequest.setLanguageCode(LanguageCode.JaJP);

        // Synchronously ask Amazon Polly to describe available TTS voices.
        final DescribeVoicesResult describeVoicesResult = this.pollyClient.describeVoices(describeVoicesRequest);
        this.voice = describeVoicesResult.getVoices().get(VOICE_ID); // 0 = female, 1 = male
    }

    public void create(final PollyTtsData ttsData) {
        //get the audio stream
        try {
            final InputStream speechStream = this.synthesize(ttsData.getSsml(), OutputFormat.Mp3);
            Files.copy(speechStream, Paths.get(ttsData.getFileName()));
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't create " + ttsData.getFileName(), e);
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // PRIVATE
    // ------------------------------------------------------------------------------------------ //

    private InputStream synthesize(final String text, final OutputFormat format) throws IOException {
        final SynthesizeSpeechRequest synthReq =
                new SynthesizeSpeechRequest().withText(text).withTextType(TextType.Ssml).withVoiceId(this.voice.getId())
                        .withOutputFormat(format);
        final SynthesizeSpeechResult synthRes = this.pollyClient.synthesizeSpeech(synthReq);

        return synthRes.getAudioStream();
    }

    private String[] loadCredentials() {

        try {
            final String file = new String(Files.readAllBytes(AWS_API_KEYS));
            final String[] keys = file.split(":");
            if (keys.length != 2) {
                throw new IllegalStateException("Key file format wrong. Need: ACCESS_KEY:SECRET_KEY");
            }
            return keys;
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't load API keys");
        }

    }

}
