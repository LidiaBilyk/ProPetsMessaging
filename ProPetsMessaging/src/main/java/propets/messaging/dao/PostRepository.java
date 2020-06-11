package propets.messaging.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import propets.messaging.model.Post;

public interface PostRepository extends MongoRepository<Post, String> {
	
	Page<Post> findAll(Pageable pageable);

}
