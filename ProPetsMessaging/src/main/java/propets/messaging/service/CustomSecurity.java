package propets.messaging.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import propets.messaging.dao.PostRepository;
import propets.messaging.exceptions.NotFoundException;
import propets.messaging.model.Post;

@Component
public class CustomSecurity {
	
	@Autowired
	PostRepository postRepository;
	
	public boolean chechAuthorityForPost(String login, Principal principal) {
		return login.equals(principal.getName());
	}
	
	public boolean checkAuthorityForDeletePost(String id, Principal principal) {
		Post post = postRepository.findById(id).orElseThrow(NotFoundException::new);		
		return post.getUserLogin().equals(principal.getName());
	}
}
