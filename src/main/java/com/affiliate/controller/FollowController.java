package com.affiliate.controller;

import com.affiliate.model.Follower;
import com.affiliate.model.User;
import com.affiliate.service.FollowService;
import com.affiliate.service.UserService;
import com.affiliate.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class FollowController {

	@Autowired
	private FollowService followService;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/follow/{followingId}")
	public ResponseEntity<?> followUser(@PathVariable Long followingId,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7); // Remove "Bearer "
		String username = jwtUtil.getUsernameFromToken(token);
		User follower = userService.getUserByUsername(username); // Assuming you have a method to get user by username

		if (follower != null) {
			followService.followUser(follower.getId(), followingId);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}

	@DeleteMapping("/unfollow/{followingId}")
	public ResponseEntity<?> unfollowUser(@PathVariable Long followingId,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7); // Remove "Bearer "
		String username = jwtUtil.getUsernameFromToken(token);
		User follower = userService.getUserByUsername(username); // Assuming you have a method to get user by username

		if (follower != null) {
			followService.unfollowUser(follower.getId(), followingId);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}

	@GetMapping("/{userId}/followers")
	public ResponseEntity<List<Follower>> getFollowers(@PathVariable Long userId) {
		List<Follower> followers = followService.getFollowers(userId);
		return new ResponseEntity<>(followers, HttpStatus.OK);
	}

	@GetMapping("/following")
	public ResponseEntity<List<Follower>> getFollowing(@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7); // Remove "Bearer "
		String username = jwtUtil.getUsernameFromToken(token);
		User follower = userService.getUserByUsername(username); // Assuming you have a method to get user by username

		if (follower != null) {
			List<Follower> following = followService.getFollowing(follower.getId());
			return new ResponseEntity<>(following, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}

	@GetMapping("/followers/count/{userId}")
	public ResponseEntity<Integer> getFollowersCount(@PathVariable Long userId) {
		int count = followService.getFollowersCount(userId);
		return new ResponseEntity<>(count, HttpStatus.OK);
	}
}