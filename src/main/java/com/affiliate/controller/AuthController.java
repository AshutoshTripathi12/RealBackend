package com.affiliate.controller;

import com.affiliate.model.Influencer;
import com.affiliate.model.User;
import com.affiliate.model.VerificationStatus;
import com.affiliate.repository.InfluencerRepository;
import com.affiliate.repository.UserRepository;
import com.affiliate.request.LoginRequest;
import com.affiliate.request.RegistrationRequest;
import com.affiliate.response.LoginResponse;
import com.affiliate.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InfluencerRepository influencerRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
		if (userRepository.existsByUsername(registrationRequest.getUsername())) {
			return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(registrationRequest.getEmail())) {
			return new ResponseEntity<>("Email is already registered!", HttpStatus.BAD_REQUEST);
		}

		User user = new User();
		user.setUsername(registrationRequest.getUsername());
		user.setEmail(registrationRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
		user.setRole("INFLUENCER"); // Assuming role is "USER" or "INFLUENCER"
		userRepository.save(user);

		Influencer influencer = new Influencer();
		influencer.setUser(user);
		influencer.setVerificationStatus(VerificationStatus.PENDING); // Initial status for influencers
		influencerRepository.save(influencer);
		return new ResponseEntity<>("Registered successfully!", HttpStatus.OK);

	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsernameOrEmail());
			String token = jwtUtil.generateToken(userDetails);
			return ResponseEntity.ok(new LoginResponse(token));
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Invalid username/email or password", HttpStatus.UNAUTHORIZED);
		}
	}
}