package telran.ProPets.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import telran.ProPets.configuration.MessagingConfiguration;
import telran.ProPets.dao.PostRepository;
import telran.ProPets.dto.PageDto;
import telran.ProPets.dto.PostDto;
import telran.ProPets.dto.PostResponseDto;
import telran.ProPets.exceptions.BadRequestException;
import telran.ProPets.exceptions.ConflictException;
import telran.ProPets.exceptions.NotFoundException;
import telran.ProPets.model.Post;

@Service
public class PostServiceImpl implements PostService {
	@Autowired
	PostRepository postRepository;
	@Autowired
	MessagingConfiguration messagingConfiguration;

	@Override
	public PostResponseDto post(String login, PostDto postDto) {		
		Post post = Post.builder()
				.userLogin(login)
				.username(postDto.getUsername())
				.avatar(postDto.getAvatar())
				.datePost(LocalDateTime.now())
				.text(postDto.getText())
				.images(postDto.getImages())
				.build();			
		postRepository.save(post);
		sendToActivities(post.getId(), post.getUserLogin(), HttpMethod.PUT);
		return postToPostResponseDto(post);
	}

	private void sendToActivities(String id, String userLogin, HttpMethod method) {
		RestTemplate restTemplate = new RestTemplate();	
		String activityTemplate = "/activity/";
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-ServiceName", messagingConfiguration.getApplicationName());
		try {
			RequestEntity<String> restRequest = new RequestEntity<>(headers, method, 			
					new URI(messagingConfiguration.getActivityUri().concat(userLogin).concat(activityTemplate).concat(id)));
			ResponseEntity<String>restResponse = restTemplate.exchange(restRequest, String.class);
		} catch (RestClientException e) {
			throw new ConflictException();
		} 
		catch (URISyntaxException e) {			
			throw new BadRequestException();
		}		
	}

	private PostResponseDto postToPostResponseDto(Post post) {		
		return PostResponseDto.builder()
				.id(post.getId())
				.userLogin(post.getUserLogin())
				.username(post.getUsername())
				.avatar(post.getAvatar())
				.text(post.getText())
				.datePost(post.getDatePost())
				.images(post.getImages())
				.build();
	}

	@Override
	public PostResponseDto getPostById(String id) {		
		Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException());		
		return postToPostResponseDto(post);
	}

	@Override
	public PostResponseDto updatePost(String id, PostResponseDto postResponseDto) {	
		Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException());		
		if (postResponseDto.getImages() != null) {
			post.setImages(postResponseDto.getImages());
		}
		if (post.getText() != null) {
			post.setText(postResponseDto.getText());
		}
		postRepository.save(post);
		return postToPostResponseDto(post);
	}
	
	@Override		
	public PostResponseDto deletePost(String id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException());		
		PostResponseDto postResponseDto = postToPostResponseDto(post);
		postRepository.deleteById(id);
		sendToActivities(post.getId(), post.getUserLogin(), HttpMethod.DELETE);
		return postResponseDto;
	}

	@Override
	public PageDto getPosts(Integer itemsOnPage, Integer currentPage) {
		Pageable pageable = PageRequest.of(currentPage, itemsOnPage, Sort.by("datePost").descending());
		Page<Post> page = postRepository.findAll(pageable);
		return pageToPageDto(page);
	}

	private PageDto pageToPageDto(Page<Post> page) {		
		return PageDto.builder()
				.itemsOnPage(page.getNumberOfElements())
				.currentPage(page.getNumber())
				.itemsTotal(page.getTotalElements())
				.posts(page.getContent().stream().map(p -> postToPostResponseDto(p)).collect(Collectors.toList()))
				.build();				
	}
//TODO
//Change URI! In app.prop & github too!!! Return ResponseEntyty with status?
	@Override
	public void complainPost(String id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException());		
		PostResponseDto postResponseDto = postToPostResponseDto(post);
		RestTemplate restTemplate = new RestTemplate();		
		try {
			RequestEntity<PostResponseDto> restRequest = new RequestEntity<PostResponseDto>(postResponseDto, HttpMethod.GET, new URI(messagingConfiguration.getComplainUri().concat(id)));
			ResponseEntity<PostResponseDto>restResponse = restTemplate.exchange(restRequest, PostResponseDto.class);
		} catch (RestClientException e) {
			throw new ConflictException();
		} catch (URISyntaxException e) {			
			throw new BadRequestException();
		}		
	}
	@Override
	public Set<PostResponseDto> getPostsForUserData(Set<String> postId) {
		Set<PostResponseDto> result = new HashSet<>();
		for (String id : postId) {
			Post post = postRepository.findById(id).orElse(null);
			if (post != null) {
				result.add(postToPostResponseDto(post));
			}			
		}			
		return result;
	}
}
