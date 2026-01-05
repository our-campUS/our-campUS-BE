package com.campus.campus.domain.userpost.policy;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.userpost.application.exception.PostAccessDeniedException;

@Component
public class PostAccessPolicy {

	public void validateAccess(User user, StudentCouncil writer) {
		if (!writer.getCouncilType().hasAccess(user, writer)) {
			throw new PostAccessDeniedException();
		}
	}
}
