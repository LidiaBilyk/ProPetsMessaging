package telran.ProPets.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDto {
    String username; 
    String avatar;
	String text;
	List<String> images;

}
