package com.b2b.AIhelper.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.b2b.AIhelper.entity.Address;
import com.b2b.AIhelper.entity.Customer;
import com.b2b.AIhelper.entity.KeralaVisionTroubleshooting;
import com.b2b.AIhelper.entity.ServiceRequest;
import com.b2b.AIhelper.repository.AddressRepository;
import com.b2b.AIhelper.repository.CustomerRepository;
import com.b2b.AIhelper.repository.ServiceRequestRepository;
import com.b2b.AIhelper.service.PincodeService;
import com.b2b.AIhelper.service.TroubleshootingService;
import com.b2b.AIhelper.utils.AllocationStatus;
import com.b2b.AIhelper.utils.Priority;
import com.b2b.AIhelper.utils.RequestStatus;
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
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

@RestController
@RequestMapping("/servicerequest")
public class ServiceRequestController {

	private final Translate translate = TranslateOptions.getDefaultInstance().getService();

	private static final String GCP_PROJECT_ID = "trim-axle-4614-u7";
	private static final String GCP_LOCATION = "us-central1";
	private static final String GEMINI_MODEL = "gemini-2.0-flash-001";
	@Autowired
	private TroubleshootingService troubleshootingService;
	private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private PincodeService pincodeService;

	@GetMapping("/mainprocess")
	public ResponseEntity<?> processRemoteWav(@RequestParam String audioUrl,
			@RequestParam(required = false) String authToken, @RequestParam String phoneNumber,
			@RequestParam String whatsappNumber, @RequestParam(required = false) String pinCode) {

		try {

			Optional<Customer> customer = customerRepository.findByPhoneNumberOrWhatsappNumber(phoneNumber,
					whatsappNumber);

			boolean isVerified = customer.isPresent();
			// Step 1: Download and transcribe
			Path tempFile = downloadAudioToTemp(audioUrl, authToken);
//			String malayalamText = transcribeAudio(tempFile.toString());
			
			String malayalamText = transcribeWithHuggingFace(tempFile.toString());
			


			// Step 2: Translate to English
			Translation translation = translate.translate(malayalamText, Translate.TranslateOption.sourceLanguage("ml"),
					Translate.TranslateOption.targetLanguage("en"));
			String englishText = translation.getTranslatedText();

			// Step 3: Save as NEW_REQUEST before calling Gemini
			ServiceRequest request = new ServiceRequest();
			request.setRequestDateTime(LocalDateTime.now());
			request.setCallInfoMalayalam(malayalamText);
			request.setCallInfoEnglish(englishText);
			request.setIssueSummary("Pending summarization");
			request.setCustomerWhatsappNumber(whatsappNumber);
			request.setCustomerDialledNumber(phoneNumber);
			request.setStatus(RequestStatus.NEW); // Initial status
			request.setPriority(Priority.MEDIUM);
			request.setAutoTroubleShootingTipsShared(false);
			request.setAllocationStatus(AllocationStatus.NO);
			request.setVerify(isVerified);
			request.setAudioUrl(audioUrl);
			if (pinCode != null && !pinCode.isEmpty()) {
				Optional<Address> existing = addressRepository.findByPin(pinCode);

				Address addressToUse = new Address();

				if (existing.isPresent()) {
					addressToUse = existing.get();
				} else {
					addressToUse = pincodeService.fetchFirstAddressFromPincode(pinCode);
					if (addressToUse != null) {
			            addressToUse = addressRepository.save(addressToUse); // ✅ Save it
			        }
				}
				if(addressToUse!=null) {
					request.getAddresses().add(addressToUse);
				}
			}
			request = serviceRequestRepository.save(request); // Save and get ID

			// Step 4: Gemini call
			String summary = summarizeWithGemini(englishText);
			String[] resultParts = summary.split("\\|");
			String issueSummary = resultParts[0].trim(); // The summarized issue
			String fix = resultParts.length > 1 ? resultParts[1].trim() : "fix not found";
			boolean foundFix = !fix.toLowerCase().contains("please proceed for manual fixing");

			// Step 5: Update existing request
			request.setIssueSummary(summary);
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

			// Configure the speech recognition request
			RecognitionConfig config = RecognitionConfig.newBuilder()
					.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).setLanguageCode("ml-IN") // Malayalam
																									// language code
					.build();

			// Set up the audio content
			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

			// Perform speech recognition
			RecognizeResponse response = speechClient.recognize(config, audio);

			// Collect the results and return the transcript
			StringBuilder resultText = new StringBuilder();
			for (SpeechRecognitionResult result : response.getResultsList()) {
				resultText.append(result.getAlternatives(0).getTranscript());
			}

			return resultText.toString(); // Return the transcription result
		}
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
		String prompt = "Please summarize the following issue in 2-3 concise English sentences. Then, using the provided data (where the first column is 'Issue' and the second column is 'suggested fix'), determine if the summarized issue has at least a 70% similarity to any entry in the 'Issue' column.\n"
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
	
	public String transcribeWithHuggingFace(String audioPath) throws Exception {
	    HttpClient client = HttpClient.newHttpClient();
	    byte[] audio = Files.readAllBytes(Paths.get(audioPath));
	    
	    HttpRequest request = HttpRequest.newBuilder()
	        .uri(new URI("https://api-inference.huggingface.co/models/openai/whisper-tiny?wait_for_model=true"))
	        .header("Authorization", "Bearer " + "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")
	        .POST(HttpRequest.BodyPublishers.ofByteArray(audio))
	        .build();

	    HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
	    return res.body(); // Contains the detected text in its original language
	}
}
