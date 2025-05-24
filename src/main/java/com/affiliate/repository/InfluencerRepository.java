package com.affiliate.repository;

import com.affiliate.model.Influencer;
import com.affiliate.model.VerificationStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface InfluencerRepository extends JpaRepository<Influencer, Long> {

	Optional<Influencer> findByUser_Id(Long userId);

	List<Influencer> findTop30ByVerificationStatusOrderByFollowerCountDesc(VerificationStatus verificationStatus,
			Pageable pageable);

	// Example method if you don't have followerCount, just fetches top 3 verified
	List<Influencer> findTop30ByVerificationStatus(VerificationStatus verificationStatus, Pageable pageable);

}