package telran.ProPets.security.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import telran.ProPets.configuration.MessagingConfiguration;


@Service
@Order(10)
public class AuthenticationFilter implements Filter{
	
	@Autowired
	MessagingConfiguration messagingConfiguration;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException, RestClientException {			
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
					
			String auth = request.getHeader("X-Token");
			HttpHeaders headers = new HttpHeaders();
			headers.add("X-Token", auth);
			RestTemplate restTemplate = new RestTemplate();	
			ResponseEntity<String> restResponse = null;
			try {
				RequestEntity<String> restRequest = new RequestEntity<>(headers, HttpMethod.GET, new URI(messagingConfiguration.getCheckJwtUri()));				
				restResponse = restTemplate.exchange(restRequest, String.class);				
			} catch (URISyntaxException e) {
				response.sendError(400, "Bad request");
			}catch (HttpClientErrorException e) {				
				if (e.getStatusCode() != HttpStatus.OK) {					
					response.sendError(401, "Header Authorization is not valid");
					return;
				}
			}
			String jwt = restResponse.getHeaders().getFirst("X-Token");				
			String login = restResponse.getHeaders().getFirst("X-Login");		
			response.addHeader("X-Token", jwt);
			response.addHeader("X-Login", login);
			
			chain.doFilter(new WrapperRequest(request, login), response);		
	}
	
	private class WrapperRequest extends HttpServletRequestWrapper {

		String user;

		public WrapperRequest(HttpServletRequest request, String user) {
			super(request);
			this.user = user;
		}

		@Override
		public Principal getUserPrincipal() {
			return new Principal() { // or return () -> user;

				@Override
				public String getName() {
					return user;
				}
			};
		}
	}
}
