package com.b2b.AIhelper.controller;

import com.google.cloud.speech.v1.*;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.protobuf.ByteString;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;

@RestController
@RequestMapping("/audio")
public class AudioInterpretController {

    private final Translate translate = TranslateOptions.getDefaultInstance().getService();
    
    // Gemini configuration - replace with your values
    private static final String GCP_PROJECT_ID = "trim-axle-461413-u7";
    private static final String GCP_LOCATION = "us-central1";
    private static final String GEMINI_MODEL = "gemini-2.0-flash-001";
    private final RestTemplate restTemplate = new RestTemplate(); // For HTTP downloads

    @GetMapping("/process")
    public String processRemoteWav(
            @RequestParam String audioUrl,
            @RequestParam(required = false) String authToken) throws Exception {

//        Path wavFilePath = Paths.get("C:\\Users\\thoma\\Downloads\\wifi_not_wkring.wav");
        Path tempFile = downloadAudioToTemp(audioUrl, authToken);

//        if (!Files.exists(wavFilePath)) {
//            throw new FileNotFoundException("Local file not found: " + wavFilePath);
//        }
        
        // Step 1: Transcribe Malayalam audio to text
        String malayalamText = transcribeAudio(tempFile.toString());
        System.out.println("Malayalam text: " + malayalamText);

        // Step 2: Translate to English
        Translation translation = translate.translate(
            malayalamText,
            Translate.TranslateOption.sourceLanguage("ml"),
            Translate.TranslateOption.targetLanguage("en")
        );
        String englishText = translation.getTranslatedText();
        System.out.println("Translated English text: " + englishText);

        // Step 3: Summarize with Gemini
        return summarizeWithGemini(englishText);
    }

    private String transcribeAudio(String filePath) throws Exception {    	
        try (SpeechClient speechClient = SpeechClient.create()) {
            byte[] data = Files.readAllBytes(Paths.get(filePath));
            ByteString audioBytes = ByteString.copyFrom(data);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("ml-IN")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            StringBuilder resultText = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                resultText.append(result.getAlternatives(0).getTranscript());
            }
            return resultText.toString();
        }
    }

    private String summarizeWithGemini(String englishText) throws IOException {
        try (VertexAI vertexAI = new VertexAI(GCP_PROJECT_ID, GCP_LOCATION)) {
            GenerativeModel model = new GenerativeModel(GEMINI_MODEL, vertexAI);
            
            String prompt = "Please summarize the following text in 2-3 concise English sentences and check in the public github excel file https://github.com/john-walt/voxsolveAI/blob/main/keralavision_test.xlsx if the similar issue is present in the Column A(Issue) and if yes give me only the Column B (Suggested fix) for the issue . If you think it does not have anything similar please send the response as - fix not found please proceed for manual fixing \n\n" 
            		
                           + englishText;
            
            GenerateContentResponse response = model.generateContent(
                ContentMaker.fromMultiModalData(prompt)
            );
            
            return ResponseHandler.getText(response);
        }
    }
    
    private Path downloadAudioToTemp(String audioUrl, String authToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(org.springframework.http.MediaType.ALL));
        
        // Add tmpfiles.org specific headers
        if (audioUrl.contains("tmpfiles.org")) {
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (compatible; SpringBootApp)");
            // Force https to avoid redirects
            if (!audioUrl.startsWith("https")) {
                audioUrl = audioUrl.replace("http://", "https://");
            }
        }
        
        if (authToken != null && !authToken.isEmpty()) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        }

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                audioUrl,
                HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(headers),
                byte[].class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IOException("Failed to download audio. HTTP Status: " + 
                                      response.getStatusCode() + " for URL: " + audioUrl);
            }
            
            if (response.getBody() == null || response.getBody().length == 0) {
                throw new IOException("Empty audio file downloaded from URL: " + audioUrl);
            }

            Path tempFile = Files.createTempFile("audio_", ".wav");
            Files.write(tempFile, response.getBody(), StandardOpenOption.WRITE);
            
            System.out.println("Downloaded audio to: " + tempFile + 
                              " Size: " + response.getBody().length + " bytes");
            return tempFile;
        } catch (RestClientException e) {
            throw new IOException("Error downloading audio from URL: " + audioUrl, e);
        }
    }
 
    
    private RestTemplate createRestTemplateWithTimeout() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setInstanceFollowRedirects(true);  // Enable automatic redirect following
            }
        };
        factory.setConnectTimeout(30_000);
        factory.setReadTimeout(120_000);
        return new RestTemplate(factory);
    }
}