package com.b2b.AIhelper.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2b.AIhelper.entity.User;
import com.b2b.AIhelper.models.LoginRequest;
import com.b2b.AIhelper.models.OtpRequest;
import com.b2b.AIhelper.models.ResponseDTO;
import com.b2b.AIhelper.models.SignupRequest;
import com.b2b.AIhelper.models.TokenRequest;
import com.b2b.AIhelper.repository.UserRepository;
import com.b2b.AIhelper.service.EmailService;
//import com.b2b.AIhelper.utils.JwtUtil;
import com.b2b.AIhelper.service.GoogleTokenVerifierService;
import com.b2b.AIhelper.utils.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/auth")
public class LoginController {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private EmailService emailService;
	@Autowired
	private GoogleTokenVerifierService tokenVerifier;

	@Autowired
	private JwtUtil jwtUtil;

//	    private final JwtUtil jwtUtil = new JwtUtil();
//	    private final Map<String, String> userCredentials = Map.of("user", "password");  // Just for demo

	public LoginController(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	@Operation(summary = "User Login", description = "Authenticates user with email and password")
	@CrossOrigin(origins = "*")
	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequest request) {
		String email = request.getEmail();
		String password = request.getPassword();

		// Find user by email
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isEmpty()) {
			ResponseDTO responseDTO = new ResponseDTO(401, "User not found", null);
			return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
		}

		User user = userOptional.get();
		Map<String, Object> data = new HashMap<>();
		data.put("userid", user.getId());
		data.put("user_name", user.getName());
		data.put("accessToken", jwtUtil.generateAccessToken(email));
	    data.put("refreshToken", jwtUtil.generateRefreshToken(email));


		// Validate password
		if (!passwordEncoder.matches(password, user.getPassword())) {
			ResponseDTO responseDTO = new ResponseDTO(401, "Invalid password", null);
			return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
		}

		ResponseDTO responseDTO = new ResponseDTO(200, "Login successful", data);
		return new ResponseEntity<>(responseDTO, HttpStatus.OK);
	}

	@Operation(summary = "Signup for with username, password and phonenumber", description = "Creates a new user and sends the OTP to the provided mail id")
	@CrossOrigin(origins = "*")
	@PostMapping("/signup")
	public ResponseEntity<ResponseDTO> signup(@RequestBody SignupRequest request) {
		String phoneNumber = request.getPhoneNumber();
		String email = request.getEmail();
		String password = request.getPassword();
		String name = request.getName();

		// Validate phone number
//        if (phoneNumber == null || phoneNumber.length() != 10) {
//        	ResponseDTO responseDTO = new ResponseDTO(500, "Invalid phone number", null);
//            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
//            
//        }

		// Check if user already exists
		if (userRepository.findByEmail(email).isPresent()) {
			ResponseDTO responseDTO = new ResponseDTO(409, "User already exists", null);
			return new ResponseEntity<>(responseDTO, HttpStatus.CONFLICT);
		}

		// Save user details to database with hashed password
		String otp = generateRandomOTP();
		String hashedOtp = hashOtp(otp);
		User newU = new User();
		System.out.println("password : " + password);
		try {
			// After generating OTP
			emailService.sendOtpEmail(email, otp);
		} catch (Exception e) {
			Map<String, Object> data = new HashMap<>();

			ResponseDTO responseDTO = new ResponseDTO(405, "Invalid mail id. Please try again", data);
			return new ResponseEntity<>(responseDTO, HttpStatus.METHOD_NOT_ALLOWED);
		}
		try {
			User newUser = new User(generateRandom10DigitValue(), email, passwordEncoder.encode(password), hashedOtp,
					name);
			newU = userRepository.save(newUser);
		} catch (Exception e) {
			if (e.getMessage().contains("duplicate key")) {
				Map<String, Object> data = new HashMap<>();

				ResponseDTO responseDTO = new ResponseDTO(409,
						"Mobile number has been already registered. Please login using the same.", data);
				return new ResponseEntity<>(responseDTO, HttpStatus.CONFLICT);
			}
		}

		// Wrap OTP inside an object
		// Generate JWT tokens after saving the user
		String accessToken = jwtUtil.generateAccessToken(email);
		String refreshToken = jwtUtil.generateRefreshToken(email);
		Map<String, Object> data = new HashMap<>();
		data.put("user_id", newU.getId());
		data.put("user_name", newU.getName());
		data.put("accessToken", accessToken);
		data.put("refreshToken", refreshToken);
		ResponseDTO responseDTO = new ResponseDTO(200, "OTP generated successfully", data);
		return new ResponseEntity<>(responseDTO, HttpStatus.OK);
	}

	@Operation(summary = "verification of the OTP send to mail ", description = "Returns true if OTP send to mail is verified")
	@CrossOrigin(origins = "*")
	@PostMapping("/getotp")
	public ResponseEntity<ResponseDTO> generateOTP(@RequestBody OtpRequest request) {
		String otp = request.getOtp();
		String email = request.getEmail();
		// Validate phone number
//        if (phoneNumber == null || phoneNumber.length() != 10) {
//        	ResponseDTO responseDTO = new ResponseDTO(500, "Invalid phone number", null);
//            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
//            
//        }

		// Check if user already exist

		Optional<User> user = userRepository.findByEmail(email);
		if (user.isPresent() && verifyOtp(otp, user.get().getOtp())) {
			ResponseDTO responseDTO = new ResponseDTO(200, "OTP verified successfully", null);
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			ResponseDTO responseDTO = new ResponseDTO(401, "invalid OTP", null);
			return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);

		}
	}

	private String generateRandomOTP() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	private String generateRandom10DigitValue() {
		Random random = new Random();
		long min = 1_000_000_000L; // smallest 10-digit number
		long max = 9_999_999_999L; // largest 10-digit number
		long number = min + ((long) (random.nextDouble() * (max - min)));
		return String.valueOf(number);
	}

	@PostMapping("/google")
	public ResponseEntity<?> authenticateWithGoogle(@RequestBody TokenRequest tokenRequest) {
		try {
			Payload payload = tokenVerifier.verify(tokenRequest.getIdToken());
			String email = payload.getEmail();
			String name = (String) payload.get("name");

			if (Boolean.TRUE.equals(payload.getEmailVerified())) {
				Optional<User> existingUser = userRepository.findByEmail(email);
				User user;

				if (existingUser.isPresent()) {
					user = existingUser.get();
				} else {
					// Create user with dummy password and no phone
					user = new User();
					user.setEmail(email);
					user.setPhoneNumber(null); // or collect from frontend later
					user.setPassword(UUID.randomUUID().toString()); // dummy password
					userRepository.save(user);
				}

				// Optionally: generate JWT and return it
				return ResponseEntity.ok("Welcome " + user.getEmail());
			} else {
				return ResponseEntity.status(403).body("Email not verified");
			}

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Invalid ID Token");
		}
	}

	public String hashOtp(String otp) {
		return passwordEncoder.encode(otp);
	}

	public boolean verifyOtp(String rawOtp, String hashedOtp) {
		return passwordEncoder.matches(rawOtp, hashedOtp);
	}

	@Operation(summary = "Refresh access token using refresh token")
	@CrossOrigin(origins = "*")
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAccessToken(@RequestBody TokenRequest request) {
		String refreshToken = request.getIdToken(); // reuse model for simplicity

		if (!jwtUtil.validateToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
		}

		String email = jwtUtil.getEmailFromToken(refreshToken);
		String newAccessToken = jwtUtil.generateAccessToken(email);

		return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
	}

}
