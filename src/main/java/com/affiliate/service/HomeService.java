package com.affiliate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.affiliate.model.Influencer;
import com.affiliate.model.Product;
import com.affiliate.model.VerificationStatus;
import com.affiliate.repository.InfluencerRepository;
import com.affiliate.repository.ProductRepository;

import java.util.List;

@Service
public class HomeService {

	@Autowired
	private InfluencerRepository influencerRepository;

	@Autowired
	private ProductRepository productRepository;

	public List<Influencer> getFeaturedInfluencers() {
		// Example: Fetch top 3 verified influencers sorted by follower count (if you
		// have followerCount)
		return influencerRepository.findTop30ByVerificationStatusOrderByFollowerCountDesc(VerificationStatus.VERIFIED,
				PageRequest.of(0, 20));
		// If you don't have followerCount, you can use other criteria or just fetch the
		// top 3 verified influencers
		// return
		// influencerRepository.findTop3ByVerificationStatus(VerificationStatus.VERIFIED,
		// PageRequest.of(0, 3));
	}

	public List<Product> getTrendingProducts() {
		// Example: Fetch the latest 4 products (you might want to implement more
		// sophisticated logic for "trending")
		return productRepository.findTop4ByOrderByCreatedAtDesc(PageRequest.of(0, 4));
	}
}