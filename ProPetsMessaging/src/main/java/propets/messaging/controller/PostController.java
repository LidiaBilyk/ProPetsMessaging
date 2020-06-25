package propets.messaging.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import propets.messaging.dto.PageDto;
import propets.messaging.dto.PostDto;
import propets.messaging.dto.PostResponseDto;
import propets.messaging.dto.UserUpdateDto;
import propets.messaging.model.Post;
import propets.messaging.service.PostService;

@RestController
@RequestMapping("/{lang}/v1")
public class PostController {
	
	@Autowired
	PostService postService;
	
	@PostMapping("/{login:.*}")
	public PostResponseDto post(@PathVariable String login, @RequestBody PostDto postDto, @RequestHeader("X-token") String token) {
		return postService.post(login, postDto);
	}

	@GetMapping("/{id:.*}")
	public PostResponseDto getPostById(@PathVariable String id, @RequestHeader("X-token") String token) {
		return postService.getPostById(id);
	}
	
	@PutMapping("/{id:.*}")
	public PostResponseDto updatePost(@PathVariable String id, @RequestBody PostResponseDto postDto, @RequestHeader("X-token") String token) {
		return postService.updatePost(id, postDto);
	}
	
	@DeleteMapping("/{id:.*}")
	public PostResponseDto deletePost(@PathVariable String id, @RequestHeader("X-token") String token) {
		return postService.deletePost(id);
	}
	
	@GetMapping("/view")
	public PageDto getPosts(@RequestParam Integer itemsOnPage, @RequestParam Integer currentPage, @RequestHeader("X-token") String token) {
		return postService.getPosts(itemsOnPage, currentPage);
	}
	
	@PutMapping("/complain/{id:.*}")
	public void complainPost(@PathVariable String id) {
		postService.complainPost(id);
	}
	
	@PostMapping("/userdata")
	public Set<PostResponseDto> getPostsForUserData(@RequestBody Set<String> postId) {
		return postService.getPostsForUserData(postId);
	}
	
	@PostMapping("/userdata/{login}")
	public Set<PostResponseDto> getPostsForUserData(@PathVariable String login) {
		return postService.getPostsForUserData(login);
	}
	
	@PutMapping("/updateuser")
	public Set<Post> updateUserPosts(@RequestBody UserUpdateDto userUpdateDto) {	
		return postService.updateUserPosts(userUpdateDto);
	}
}
