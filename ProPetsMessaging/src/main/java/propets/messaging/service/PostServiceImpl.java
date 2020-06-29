package propets.messaging.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import propets.messaging.configuration.MessagingConfiguration;
import propets.messaging.dao.PostRepository;
import propets.messaging.dto.PageDto;
import propets.messaging.dto.PostDto;
import propets.messaging.dto.PostResponseDto;
import propets.messaging.exceptions.BadRequestException;
import propets.messaging.exceptions.ConflictException;
import propets.messaging.exceptions.NotFoundException;
import propets.messaging.model.Post;

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
		return postToPostResponseDto(post);
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
}
