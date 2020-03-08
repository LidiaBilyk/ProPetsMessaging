package telran.ProPets.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDto {
	String text;
	List<String> images;

}
