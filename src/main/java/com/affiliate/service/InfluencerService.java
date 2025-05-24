package com.affiliate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.affiliate.model.Influencer;
import com.affiliate.model.Product;
import com.affiliate.model.User;
import com.affiliate.repository.InfluencerRepository;
import com.affiliate.repository.ProductRepository;
import com.affiliate.repository.UserRepository;
import com.affiliate.request.InfluencerUpdateRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InfluencerService {

	@Autowired
	private InfluencerRepository influencerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

	public List<Influencer> getAllInfluencers() {
		return influencerRepository.findAll();
	}

	public Optional<Influencer> getInfluencerById(Long id) {
		return influencerRepository.findById(id);
	}

	public Influencer getInfluencerProfile(String username) {
		Optional<User> userOptional = userRepository.findByUsername(username);
		return userOptional.map(user -> influencerRepository.findByUser_Id(user.getId()).orElse(null)).orElse(null);
	}

	public Influencer updateInfluencerProfile(String username, InfluencerUpdateRequest updateRequest) {
		Optional<User> userOptional = userRepository.findByUsername(username);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			Optional<Influencer> influencerOptional = influencerRepository.findByUser_Id(user.getId());
			influencerOptional.ifPresent(influencer -> {
				if (updateRequest.getProfileBio() != null)
					influencer.setProfileBio(updateRequest.getProfileBio());
				if (updateRequest.getSocialMediaLinks() != null)
					influencer.setSocialMediaLinks(updateRequest.getSocialMediaLinks());
				// profilePictureUrl removed from updateRequest
				// genre to be handled
				influencerRepository.save(influencer);
			});
			return influencerOptional.orElse(null);
		}
		return null;
	}

	public String uploadProfilePhoto(String username, MultipartFile photo) throws IOException {
		Optional<User> userOptional = userRepository.findByUsername(username);
		if (!userOptional.isPresent()) {
			throw new UsernameNotFoundException("User not found: " + username);
		}
		User user = userOptional.get();
		Optional<Influencer> influencerOptional = influencerRepository.findByUser_Id(user.getId());
		if (!influencerOptional.isPresent()) {
			throw new RuntimeException("Influencer profile not found for user: " + username);
		}
		Influencer influencer = influencerOptional.get();
		return savePhoto(photo, influencer, null);
	}

	public String uploadProductPhoto(String username, Long productId, MultipartFile photo) throws IOException {
		Optional<User> userOptional = userRepository.findByUsername(username);
		if (!userOptional.isPresent()) {
			throw new UsernameNotFoundException("User not found: " + username);
		}
		User user = userOptional.get();
		Optional<Influencer> influencerOptional = influencerRepository.findByUser_Id(user.getId());
		if (!influencerOptional.isPresent()) {
			throw new RuntimeException("Influencer profile not found for user: " + username);
		}
		Influencer influencer = influencerOptional.get();
		Optional<Product> productOptional = productRepository.findById(productId);
		if (!productOptional.isPresent()) {
			throw new IllegalArgumentException("Product not found with ID: " + productId);
		}
		Product product = productOptional.get();
		if (!product.getInfluencer().getId().equals(influencer.getId())) {
			throw new IllegalAccessError("Product does not belong to the logged-in influencer.");
		}
		return savePhoto(photo, null, product);
	}

	public Product addProductToProfile(String username, String name, String affiliateLink, MultipartFile photo)
			throws IOException {
		Optional<User> userOptional = userRepository.findByUsername(username);
		if (!userOptional.isPresent()) {
			throw new UsernameNotFoundException("User not found: " + username);
		}
		User user = userOptional.get();
		Optional<Influencer> influencerOptional = influencerRepository.findByUser_Id(user.getId());
		if (!influencerOptional.isPresent()) {
			throw new RuntimeException("Influencer profile not found for user: " + username);
		}
		Influencer influencer = influencerOptional.get();
		Product newProduct = new Product();
		newProduct.setName(name);
		newProduct.setAffiliateLink(affiliateLink);
		newProduct.setInfluencer(influencer);

		// Set the description here
		newProduct.setDescription("Default product description"); // Example: Setting a default description
		newProduct.setCommissionRate(java.math.BigDecimal.ZERO); // Make sure commissionRate is also set

		if (photo != null && !photo.isEmpty()) {
			String imageUrl = savePhoto(photo, null, newProduct);
			newProduct.setImageUrl(imageUrl);
		}
		return productRepository.save(newProduct);
	}

	private String savePhoto(MultipartFile photo, Influencer influencer, Product product) throws IOException {
		String fileName = StringUtils.cleanPath(photo.getOriginalFilename());
		String fileExtension = "";
		if (fileName.contains(".")) {
			fileExtension = fileName.substring(fileName.lastIndexOf("."));
		}
		String newFileName = UUID.randomUUID().toString() + fileExtension;
		Path targetLocation = this.fileStorageLocation.resolve(newFileName);
		Files.createDirectories(fileStorageLocation);
		Files.copy(photo.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

		// Construct the complete URL
		String baseUrl = "http://localhost:8080"; // Replace with your actual back-end base URL
		String photoUrl = baseUrl + "/uploads/" + newFileName;

		if (influencer != null) {
			influencer.setProfilePictureUrl(photoUrl);
			influencerRepository.save(influencer);
		} else if (product != null) {
			product.setImageUrl(photoUrl);
			productRepository.save(product);
		}
		return photoUrl;
	}
}