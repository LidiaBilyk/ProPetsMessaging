package propets.messaging.service;

import java.util.Set;

import propets.messaging.dto.PageDto;
import propets.messaging.dto.PostDto;
import propets.messaging.dto.PostResponseDto;
import propets.messaging.dto.UserUpdateDto;
import propets.messaging.model.Post;


public interface PostService {
	PostResponseDto post (String login, PostDto postDto);
	PostResponseDto getPostById(String id);
	PostResponseDto updatePost(String id, PostResponseDto postDto);
	PostResponseDto deletePost(String id);
	PageDto getPosts(Integer itemsOnPage, Integer currentPage);
	void complainPost(String id);
	Set<PostResponseDto> getPostsForUserData(Set<String> postId);
	Set<PostResponseDto> getPostsForUserData(String login);
	Set<Post> updateUserPosts(UserUpdateDto userUpdateDto);
}
