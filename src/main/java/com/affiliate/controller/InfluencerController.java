package com.affiliate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.affiliate.model.Influencer;
import com.affiliate.model.Product;
import com.affiliate.request.InfluencerUpdateRequest;
import com.affiliate.service.InfluencerService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/influencers")
public class InfluencerController {

	@Autowired
	private InfluencerService influencerService;

	@GetMapping
	public ResponseEntity<List<Influencer>> getAllInfluencers() {
		List<Influencer> influencers = influencerService.getAllInfluencers();
		return new ResponseEntity<>(influencers, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Influencer> getInfluencerById(@PathVariable Long id) {
		return influencerService.getInfluencerById(id)
				.map(influencer -> new ResponseEntity<>(influencer, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/profile")
	public ResponseEntity<?> getInfluencerProfile(@AuthenticationPrincipal UserDetails userDetails) {
		Influencer influencer = influencerService.getInfluencerProfile(userDetails.getUsername());
		if (influencer != null) {
			return new ResponseEntity<>(influencer, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Influencer profile not found", HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/profile")
	public ResponseEntity<?> updateInfluencerProfile(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody InfluencerUpdateRequest updateRequest) {
		Influencer updatedInfluencer = influencerService.updateInfluencerProfile(userDetails.getUsername(),
				updateRequest);
		if (updatedInfluencer != null) {
			return new ResponseEntity<>(updatedInfluencer, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Failed to update influencer profile", HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/profile/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadProfilePhoto(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("photo") MultipartFile photo) {
		try {
			String photoUrl = influencerService.uploadProfilePhoto(userDetails.getUsername(), photo);
			return new ResponseEntity<>(photoUrl, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>("Failed to upload photo", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/profile/upload-product-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadProductPhoto(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("photo") MultipartFile photo, @RequestParam("productId") Long productId) {
		try {
			String photoUrl = influencerService.uploadProductPhoto(userDetails.getUsername(), productId, photo);
			return new ResponseEntity<>(photoUrl, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>("Failed to upload product photo", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/profile/add-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addProductToProfile(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("name") String name, @RequestParam("affiliateLink") String affiliateLink,
			@RequestParam(value = "photo", required = false) MultipartFile photo) {
		try {
			Product addedProduct = influencerService.addProductToProfile(userDetails.getUsername(), name, affiliateLink,
					photo);
			if (addedProduct != null) {
				return new ResponseEntity<>(addedProduct, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>("Failed to add product", HttpStatus.BAD_REQUEST);
			}
		} catch (IOException e) {
			return new ResponseEntity<>("Failed to upload product photo", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}