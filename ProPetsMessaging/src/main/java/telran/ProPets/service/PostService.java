package telran.ProPets.service;

import java.util.Set;

import telran.ProPets.dto.PageDto;
import telran.ProPets.dto.PostDto;
import telran.ProPets.dto.PostResponseDto;


public interface PostService {
	PostResponseDto post (String login, PostDto postDto);
	PostResponseDto getPostById(String id);
	PostResponseDto updatePost(String id, PostResponseDto postDto);
	PostResponseDto deletePost(String id);
	PageDto getPosts(Integer itemsOnPage, Integer currentPage);
	void complainPost(String id);
	Set<PostResponseDto> getPostsForUserData(Set<String> postId);
}
