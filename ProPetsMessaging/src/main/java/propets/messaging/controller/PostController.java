package propets.messaging.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import propets.messaging.dto.PageDto;
import propets.messaging.dto.PostDto;
import propets.messaging.dto.PostResponseDto;
import propets.messaging.service.PostService;

@RestController
@RequestMapping("/{lang}/v1")
public class PostController {
	
	@Autowired
	PostService postService;
	
	@PostMapping("/{login:.*}")
	public PostResponseDto post(@PathVariable String login, @RequestBody PostDto postDto) {
		return postService.post(login, postDto);
	}

	@GetMapping("/{id:.*}")
	public PostResponseDto getPostById(@PathVariable String id) {
		return postService.getPostById(id);
	}
	
	@PutMapping("/{id:.*}")
	public PostResponseDto updatePost(@PathVariable String id, @RequestBody PostResponseDto postDto) {
		return postService.updatePost(id, postDto);
	}
	
	@DeleteMapping("/{id:.*}")
	public PostResponseDto deletePost(@PathVariable String id) {
		return postService.deletePost(id);
	}
	
	@GetMapping("/view")
	public PageDto getPosts(@RequestParam Integer itemsOnPage, @RequestParam Integer currentPage) {
		return postService.getPosts(itemsOnPage, currentPage);
	}
	
	@PutMapping("/complain/{id:.*}")
	public void complainPost(@PathVariable String id) {
		postService.complainPost(id);
	}
}
