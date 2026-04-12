package com.campus.campus.domain.manager.application.dto.response;

public record StudentCouncilPendingEmailResponse(
	Long councilId,
	String councilName,
	String currentEmail,
	String pendingEmail,
	String electionImageUrl
) {
}
