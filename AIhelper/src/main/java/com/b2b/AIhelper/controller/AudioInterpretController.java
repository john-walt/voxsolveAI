package com.b2b.AIhelper.controller;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Blob;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.protobuf.ByteString;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.b2b.AIhelper.entity.KeralaVisionTroubleshooting;
import com.b2b.AIhelper.service.TroubleshootingService;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Blob;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

@RestController
@RequestMapping("/audio")
public class AudioInterpretController {

	private final static Translate translate = TranslateOptions.getDefaultInstance().getService();

	// Gemini configuration - replace with your values
	private static final String GCP_PROJECT_ID = "trim-axle-461413-u7";
	private static final String GCP_LOCATION = "us-central1";
	private static final String GEMINI_MODEL = "gemini-2.0-flash-001";
	private final RestTemplate restTemplate = new RestTemplate(); // For HTTP downloads
	@Autowired
	private TroubleshootingService troubleshootingService;

	@GetMapping("/process")
	public String processRemoteWav(@RequestParam String audioUrl) throws Exception {
		String authToken = null;
//        Path wavFilePath = Paths.get("C:\\Users\\thoma\\Downloads\\wifi_not_wkring.wav");
//		Path tempFile = downloadAudioToTemp(audioUrl, authToken);

//        if (!Files.exists(wavFilePath)) {
//            throw new FileNotFoundException("Local file not found: " + wavFilePath);
//        }

		// Step 1: Transcribe Malayalam audio to text
//		String text = transcribeAudio(tempFile.toString());
		byte[] data = convertAudioToBytes(audioUrl, authToken);
		String audioBase64 = Base64.getEncoder().encodeToString(data);
		System.out.println("TEXT :" + audioBase64);
//		String text = transcribeWithHuggingFace(tempFile.toString());

		// Automatically detect language and translate
//		Translation translation1 = translate.translate(text, Translate.TranslateOption.targetLanguage("en") // only
//																														// target
//																														// language
//		);

//		System.out.println("Detected language: " + translation1.getSourceLanguage());
//		System.out.println("Translated text: " + translation1.getTranslatedText());
//		System.out.println("Main text: " + text);
//
//		// Step 2: Translate to English
//		Translation translation = translate.translate(text,
//				Translate.TranslateOption.sourceLanguage(translation1.getSourceLanguage()),
//				Translate.TranslateOption.targetLanguage("en"));
//		String englishText = translation.getTranslatedText();
		System.out.println("Translated English text: " + audioBase64);

		// Step 3: Summarize with Gemini
		return summarizeWithGemini1(data).replace("[", "").replace("]", "");
	}

	private String summarizeWithGemini1(byte[] data) throws IOException {
		List<KeralaVisionTroubleshooting> entries = troubleshootingService.getAllTroubleshootingEntries();

		// Build CSV data
		StringBuilder csvDataBuilder = new StringBuilder();
		csvDataBuilder.append("Issue,suggested fix\n");
		for (KeralaVisionTroubleshooting entry : entries) {
			String issueEscaped = escapeCsvField(entry.getIssueDescription());
			String fixEscaped = escapeCsvField(entry.getSuggestedFix());
			csvDataBuilder.append("\"").append(issueEscaped).append("\",\"").append(fixEscaped).append("\"\n");
		}
		String excelDataString = csvDataBuilder.toString();

		// Prompt text
		String prompt = "Please transcribe the provided audio (it may be in any language), "
				+ "translate it to English, and then summarize it in 2-3 concise English sentences. "
				+ "Using the provided data (where the first column is 'Issue' and the second column is 'suggested fix'), "
				+ "determine if the summarized issue has at least a 70% similarity to any entry in the 'Issue' column.\n"
				+ "The provided data is in CSV format, with the first row being headers.\n"
				+ "Give me the response in ONLY one of the following formats:\n"
				+ "1. In case a similar issue (>= 70% match) is found:\n"
				+ "   `[Summarized 2-3 lines of the issue] | [Exact fix from the 'suggested fix' column]`\n"
				+ "2. in case no similar issue (< 70% match) is found:\n"
				+ "   `[Summarized 2-3 lines of the issue] | fix not found, please proceed for manual fixing.`\n\n"
				+ "**PROVIDED DATA (CSV FORMAT):**\n. Please do not provide the breakdown, translation or anything else. only provide the response in 1. or 2. format pls"
				+ excelDataString;

		// Create audio part
		Part audioPart = Part.newBuilder().setInlineData(Blob.newBuilder().setMimeType("audio/wav") // Change if MP3 or
																									// other
				.setData(ByteString.copyFrom(data)).build()).build();

		// Create text part
		Part textPart = Part.newBuilder().setText(prompt).build();

		// Call Gemini
		try (VertexAI vertexAI = new VertexAI(GCP_PROJECT_ID, GCP_LOCATION)) {
			GenerativeModel model = new GenerativeModel(GEMINI_MODEL, vertexAI);

			GenerateContentResponse response = model.generateContent(Content.newBuilder().setRole("user") // ✅ REQUIRED
					.addParts(textPart).addParts(audioPart).build());

			return ResponseHandler.getText(response);
		}
	}

	private String transcribeAudio(String filePath) throws Exception {
		// Load credentials and set up the SpeechClient with the custom settings
		GoogleCredentials credentials = GoogleCredentials
				.fromStream(new FileInputStream("C:\\Projects\\VoxsolveAI\\trim-axle-461413-u7-3a0b59b8fe3a.json"))
				.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

		SpeechSettings speechSettings = SpeechSettings.newBuilder()
				.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();

		// Use try-with-resources to automatically close the SpeechClient
		try (SpeechClient speechClient = SpeechClient.create(speechSettings)) {
			byte[] data = Files.readAllBytes(Paths.get(filePath));
			ByteString audioBytes = ByteString.copyFrom(data);

			String primaryLanguage = "ml-IN";
			List<String> secondaryLanguage = Arrays.asList("en-IN", "ta-IN", "ml-IN", "te-IN", "kn-IN");

			String roughTranscript = runSTT(speechClient, audioBytes, primaryLanguage, secondaryLanguage);
			System.out.println(roughTranscript);
			String detectedLang = detectLanguage(roughTranscript);
			System.out.println("Detected language code: " + detectedLang);

			return runSTT(speechClient, audioBytes, detectedLang, secondaryLanguage);
			// Collect the results and return the transcript
//	            StringBuilder resultText = new StringBuilder();
//	            for (SpeechRecognitionResult result : response.getResultsList()) {
//	                resultText.append(result.getAlternatives(0).getTranscript());
//	            }
//	
//	            return resultText.toString();  // Return the transcription result
		}
	}

	private static String detectLanguage(String text) {
//	        Translate translate = TranslateOptions.newBuilder()
//	                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_PATH)))
//	                .build()
//	                .getService();

		Translation translation = translate.translate(text, Translate.TranslateOption.targetLanguage("en"));
		return translation.getSourceLanguage();
	}

	private String runSTT(SpeechClient speechClient, ByteString audioBytes, String primaryLanguage,
			List<String> secondaryLanguage) {
		// Configure the speech recognition request
		RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
				.setLanguageCode("ml-IN") // Malayalam language code
				.addAlternativeLanguageCodes("ta-IN").setLanguageCode(primaryLanguage)
				.addAllAlternativeLanguageCodes(secondaryLanguage).build();
		// Set up the audio content
		RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

		// Perform speech recognition
		RecognizeResponse response = speechClient.recognize(config, audio);

		StringBuilder transcriptBuilder = new StringBuilder();
		String detectedLanguage = null;

		for (SpeechRecognitionResult result : response.getResultsList()) {
			if (!result.getAlternativesList().isEmpty()) {
				transcriptBuilder.append(result.getAlternatives(0).getTranscript());
			}
			// This is where we get the detected language
			detectedLanguage = result.getLanguageCode();
			System.out.println("detected languafe :" + detectedLanguage);
		}

		return transcriptBuilder.toString();
	}

	private String summarizeWithGemini(String englishText) throws IOException {
		List<KeralaVisionTroubleshooting> entries = troubleshootingService.getAllTroubleshootingEntries();

		// 2. Format the fetched data into a CSV-like string for Gemini
		StringBuilder csvDataBuilder = new StringBuilder();
		csvDataBuilder.append("Issue,suggested fix\n"); // Add header row

		for (KeralaVisionTroubleshooting entry : entries) {
			// Escape double quotes within the fields and then wrap them in double quotes
			String issueEscaped = escapeCsvField(entry.getIssueDescription());
			String fixEscaped = escapeCsvField(entry.getSuggestedFix());
			csvDataBuilder.append("\"").append(issueEscaped).append("\"").append(",\"").append(fixEscaped)
					.append("\"\n");
		}
		String excelDataString = csvDataBuilder.toString();

		// 3. Construct the prompt dynamically
		String prompt = "Please summarize the following issue ( the issue might be provided in a different language. Please first translate it to english ) in 2-3 concise English sentences. Then, using the provided data (where the first column is 'Issue' and the second column is 'suggested fix'), determine if the summarized issue has at least a 70% similarity to any entry in the 'Issue' column.\n"
				+ "The provided data is in CSV format, with the first row being headers.\n"
				+ "give me response in only one of the following formats:\n"
				+ "1.  **If a similar issue (>= 70% match) is found:**\n"
				+ "    `[Summarized 2-3 lines of the issue] | [Exact fix from the 'suggested fix' column]`\n"
				+ "2.  **If no similar issue (< 70% match) is found:**\n"
				+ "    `[Summarized 2-3 lines of the issue] | fix not found, please proceed for manual fixing.`\n\n"
				+ "**ISSUE TO ANALYZE:**\n" + englishText + "\n\n" + "**PROVIDED DATA (CSV FORMAT):**\n"
				+ excelDataString;

		// 4. Call Gemini
		try (VertexAI vertexAI = new VertexAI(GCP_PROJECT_ID, GCP_LOCATION)) {
			GenerativeModel model = new GenerativeModel(GEMINI_MODEL, vertexAI);

			GenerateContentResponse response = model.generateContent(ContentMaker.fromMultiModalData(prompt));

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
			ResponseEntity<byte[]> response = restTemplate.exchange(audioUrl, HttpMethod.GET,
					new org.springframework.http.HttpEntity<>(headers), byte[].class);

			if (!response.getStatusCode().is2xxSuccessful()) {
				throw new IOException(
						"Failed to download audio. HTTP Status: " + response.getStatusCode() + " for URL: " + audioUrl);
			}

			if (response.getBody() == null || response.getBody().length == 0) {
				throw new IOException("Empty audio file downloaded from URL: " + audioUrl);
			}

			Path tempFile = Files.createTempFile("audio_", ".wav");
			Files.write(tempFile, response.getBody(), StandardOpenOption.WRITE);

			System.out.println("Downloaded audio to: " + tempFile + " Size: " + response.getBody().length + " bytes");
			return tempFile;
		} catch (RestClientException e) {
			throw new IOException("Error downloading audio from URL: " + audioUrl, e);
		}
	}

	private byte[] convertAudioToBytes(String audioUrl, String authToken) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(org.springframework.http.MediaType.ALL));

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
			ResponseEntity<byte[]> res = restTemplate.exchange(audioUrl, HttpMethod.GET,
					new org.springframework.http.HttpEntity<>(headers), byte[].class);

			if (!res.getStatusCode().is2xxSuccessful()) {
				throw new IOException("HTTP " + res.getStatusCode() + " for URL: " + audioUrl);
			}
			byte[] body = res.getBody();
			if (body == null || body.length == 0) {
				throw new IOException("Empty audio from URL: " + audioUrl);
			}
			return body; // feed this directly to your converter/transcriber
		} catch (RestClientException e) {
			throw new IOException("Error downloading audio from URL: " + audioUrl, e);
		}
	}

	private RestTemplate createRestTemplateWithTimeout() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
			@Override
			protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
				super.prepareConnection(connection, httpMethod);
				connection.setInstanceFollowRedirects(true); // Enable automatic redirect following
			}
		};
		factory.setConnectTimeout(30_000);
		factory.setReadTimeout(120_000);
		return new RestTemplate(factory);
	}

	private String escapeCsvField(String field) {
		if (field == null) {
			return "";
		}
		// Check if the field requires quoting: contains comma, double quote, or newline
		if (field.contains("\"") || field.contains(",") || field.contains("\n") || field.contains("\r")) {
			// Escape internal double quotes by doubling them
			String escapedField = field.replace("\"", "\"\"");
			// Enclose the whole field in double quotes
			return escapedField;
		}
		return field; // No escaping or quoting needed
	}

	public static String transcribeWithHuggingFace(String audioPath) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		byte[] data = Files.readAllBytes(Paths.get(audioPath));
		System.out.println("Byte data length: " + data.length);

		// Hugging Face model URL
		String apiUrl = "https://api-inference.huggingface.co/models/openai/whisper-large-v3";
		String hfToken = ""; // Replace with your Hugging Face token

		// Create HTTP connection
		HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Bearer " + hfToken);
		conn.setRequestProperty("Content-Type", "audio/wav");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);

		// Timeout settings (30 seconds)
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(30000);

		// Send audio data
		try (OutputStream os = conn.getOutputStream()) {
			os.write(data);
		}

		// Check for errors (HTTP 400, 500, etc.)
		int responseCode = conn.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
				String inputLine;
				StringBuilder errorResponse = new StringBuilder();
				while ((inputLine = errorReader.readLine()) != null) {
					errorResponse.append(inputLine);
				}
				System.err.println("Error Response: " + errorResponse.toString());
				return "Error: " + responseCode;
			}
		}

		// Read the response from the API
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Parse the detected language from the response
		String detectedLang = "";
		String responseString = response.toString();
		System.out.println("API Response: " + responseString);

		return responseString;
	}
}