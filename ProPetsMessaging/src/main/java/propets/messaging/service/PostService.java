package propets.messaging.service;

import propets.messaging.dto.PageDto;
import propets.messaging.dto.PostDto;
import propets.messaging.dto.PostResponseDto;

public interface PostService {
	PostResponseDto post (String login, PostDto postDto);
	PostResponseDto getPostById(String id);
	PostResponseDto updatePost(String id, PostResponseDto postDto);
	PostResponseDto deletePost(String id);
	PageDto getPosts(Integer itemsOnPage, Integer currentPage);
	void complainPost(String id);
}
