package com.affiliate.repository;

import com.affiliate.model.Follower;
import com.affiliate.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follower, Long> {
	boolean existsByFollowerAndFollowing(User follower, User following);

	void deleteByFollowerAndFollowing(User follower, User following);

	List<Follower> findByFollowing(User following);

	List<Follower> findByFollower(User follower);

	int countByFollowing(User following);
}