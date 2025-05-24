package com.affiliate.request;

import lombok.Data;

@Data
public class InfluencerUpdateRequest {
	public String getProfileBio() {
		return profileBio;
	}

	public void setProfileBio(String profileBio) {
		this.profileBio = profileBio;
	}

	public String getSocialMediaLinks() {
		return socialMediaLinks;
	}

	public void setSocialMediaLinks(String socialMediaLinks) {
		this.socialMediaLinks = socialMediaLinks;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	public String getVerificationStatus() {
		return verificationStatus;
	}

	public void setVerificationStatus(String verificationStatus) {
		this.verificationStatus = verificationStatus;
	}

	private String profileBio;
	private String socialMediaLinks;
	private String profilePictureUrl;
	private String verificationStatus; // Or VerificationStatus verificationStatus; if using enum
	// Add other fields you want to allow updating
}