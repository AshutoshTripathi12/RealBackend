package com.affiliate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.affiliate.model.Influencer;
import com.affiliate.model.Product;
import com.affiliate.service.HomeService;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {

	@Autowired
	private HomeService homeService;

	@GetMapping("/featured-influencers")
	public List<Influencer> getFeaturedInfluencers() {
		return homeService.getFeaturedInfluencers();
	}

	@GetMapping("/trending-products")
	public List<Product> getTrendingProducts() {
		return homeService.getTrendingProducts();
	}
}