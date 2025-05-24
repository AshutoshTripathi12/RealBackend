package com.affiliate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "followers")
public class Follower {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "follower_id", referencedColumnName = "id")
	private User follower;

	@ManyToOne
	@JoinColumn(name = "following_id", referencedColumnName = "id")
	private User following;

	// Constructors, Getters, and Setters

	public Follower() {
	}

	public Follower(User follower, User following) {
		this.follower = follower;
		this.following = following;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getFollower() {
		return follower;
	}

	public void setFollower(User follower) {
		this.follower = follower;
	}

	public User getFollowing() {
		return following;
	}

	public void setFollowing(User following) {
		this.following = following;
	}
}