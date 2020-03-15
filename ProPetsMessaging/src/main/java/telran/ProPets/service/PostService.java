package telran.ProPets.service;

import java.security.Principal;

import telran.ProPets.dto.PageDto;
import telran.ProPets.dto.PostDto;
import telran.ProPets.dto.PostResponseDto;


public interface PostService {
	PostResponseDto post (Principal principal, String login, PostDto postDto);
	PostResponseDto getPostById(String id);
	PostResponseDto updatePost(Principal principal, String id, PostResponseDto postDto);
	PostResponseDto deletePost(Principal principal, String id);
	PageDto getPosts(Integer itemsOnPage, Integer currentPage);
	

}
