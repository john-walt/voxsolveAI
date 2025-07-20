package com.b2b.AIhelper.controller;

import com.b2b.AIhelper.entity.ServiceRequest;
import com.b2b.AIhelper.repository.ServiceRequestRepository;
import com.b2b.AIhelper.utils.RequestStatus;
import com.b2b.AIhelper.utils.AllocationStatus;
import com.b2b.AIhelper.utils.Priority;

import com.google.cloud.speech.v1.*;
import com.google.cloud.translate.*;
import com.google.protobuf.ByteString;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/servicerequest")
public class ServiceRequestController {

    private final Translate translate = TranslateOptions.getDefaultInstance().getService();

    private static final String GCP_PROJECT_ID = "trim-axle-4614-u7";
    private static final String GCP_LOCATION = "us-central1";
    private static final String GEMINI_MODEL = "gemini-2.0-flash-001";
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @GetMapping("/mainprocess")
    public ResponseEntity<?> processRemoteWav(
            @RequestParam String audioUrl,
            @RequestParam(required = false) String authToken,
            @RequestParam String phoneNumber,
            @RequestParam String whatsappNumber) {

        try {
            // Step 1: Download and transcribe
            Path tempFile = downloadAudioToTemp(audioUrl, authToken);
            String malayalamText = transcribeAudio(tempFile.toString());

            // Step 2: Translate to English
            Translation translation = translate.translate(
                    malayalamText,
                    Translate.TranslateOption.sourceLanguage("ml"),
                    Translate.TranslateOption.targetLanguage("en")
            );
            String englishText = translation.getTranslatedText();

            // Step 3: Save as NEW_REQUEST before calling Gemini
            ServiceRequest request = new ServiceRequest();
            request.setRequestDateTime(LocalDateTime.now());
            request.setCallInfoMalayalam(malayalamText);
            request.setCallInfoEnglish(englishText);
            request.setRequirementRelatedTo("Pending summarization");
            request.setRequestorName(phoneNumber);
            request.setReceivedFrom(whatsappNumber);
            request.setStatus(RequestStatus.NEW);  // Initial status
            request.setPriority(Priority.MEDIUM);
            request.setAutoTroubleShootingTipsShared(false);
            request.setAllocationStatus(AllocationStatus.NO);
            request = serviceRequestRepository.save(request);  // Save and get ID

            // Step 4: Gemini call
            String summary = summarizeWithGemini(englishText);
            boolean foundFix = !summary.toLowerCase().contains("fix not found");

            // Step 5: Update existing request
            request.setRequirementRelatedTo(summary);
            request.setAutoTroubleShootingTipsShared(foundFix);
            request.setStatus(foundFix ? RequestStatus.BOT_IN_PROGRESS : RequestStatus.IN_PROGRESS);
            serviceRequestRepository.save(request);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing audio: " + e.getMessage());
        }
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

            String prompt = "Please summarize the following text in 2-3 concise English sentences and check in the public github excel file https://github.com/john-walt/voxsolveAI/blob/main/keralavision_test.xlsx if the similar issue is present in the Column A(Issue) and if yes give me only the Column B (Suggested fix) for the issue. If you think it does not have anything similar please send the response as - fix not found please proceed for manual fixing.\n\n"
                    + englishText;

            GenerateContentResponse response = model.generateContent(
                    ContentMaker.fromMultiModalData(prompt)
            );

            return ResponseHandler.getText(response);
        }
    }

    private Path downloadAudioToTemp(String audioUrl, String authToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.ALL));

        if (audioUrl.contains("tmpfiles.org")) {
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (compatible; SpringBootApp)");
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
                    new HttpEntity<>(headers),
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
            return tempFile;

        } catch (RestClientException e) {
            throw new IOException("Error downloading audio from URL: " + audioUrl, e);
        }
    }
}
