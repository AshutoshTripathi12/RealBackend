package com.affiliate.service;

import com.affiliate.model.Follower;
import com.affiliate.model.User;
import com.affiliate.repository.FollowRepository;
import com.affiliate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowService {

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private UserRepository userRepository;

	public void followUser(Long followerId, Long followingId) {
		Optional<User> followerOptional = userRepository.findById(followerId);
		Optional<User> followingOptional = userRepository.findById(followingId);

		if (followerOptional.isPresent() && followingOptional.isPresent()) {
			User follower = followerOptional.get();
			User following = followingOptional.get();
			if (!followRepository.existsByFollowerAndFollowing(follower, following)) {
				followRepository.save(new Follower(follower, following));
			}
		}
	}

	public void unfollowUser(Long followerId, Long followingId) {
		Optional<User> followerOptional = userRepository.findById(followerId);
		Optional<User> followingOptional = userRepository.findById(followingId);

		if (followerOptional.isPresent() && followingOptional.isPresent()) {
			User follower = followerOptional.get();
			User following = followingOptional.get();
			followRepository.deleteByFollowerAndFollowing(follower, following);
		}
	}

	public List<Follower> getFollowers(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		return userOptional.map(followRepository::findByFollowing).orElse(List.of());
	}

	public List<Follower> getFollowing(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		return userOptional.map(followRepository::findByFollower).orElse(List.of());
	}

	public int getFollowersCount(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		return userOptional.map(followRepository::countByFollowing).orElse(0);
	}
}