package propets.messaging.security.filter;

import java.io.IOException;
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
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import propets.messaging.configuration.MessagingConfiguration;
import propets.messaging.service.CustomSecurity;

@Service
@Order(20)
public class CustomFilter implements Filter {

	@Autowired
	CustomSecurity customSecurity;

	@Autowired
	MessagingConfiguration messagingConfiguration;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		if (!checkPointCut(path, method)) {
			UriTemplate templateLogin = new UriTemplate(messagingConfiguration.getTemplateLogin());
			UriTemplate templateId = new UriTemplate(messagingConfiguration.getTemplateId());
			Principal principal = request.getUserPrincipal();
			if (checkPointCutLogin(path, method)) {
				String pathLogin = templateLogin.match(request.getRequestURI()).get("login");
				if (!customSecurity.chechAuthorityForPost(pathLogin, principal)) {
					response.sendError(403, "Access denied");
					return;
				}
			}
			if (checkPointCutId(path, method)) {
				String pathId = templateId.match(request.getRequestURI()).get("id");
				try {
					if (!customSecurity.checkAuthorityForDeletePost(pathId, principal)) {
						response.sendError(403, "Access denied");
						return;
					}
				} catch (Exception e) {
					response.sendError(404, "Not found");
					return;
				}
			}
			chain.doFilter(new WrapperRequest(request, principal.getName()), response);
			return;
		}
		chain.doFilter(request, response);
	}

	private boolean checkPointCut(String path, String method) {
		boolean check = path.matches(".*/userdata");
		return check;
	}

//	check updatePost & deletePost methods
	private boolean checkPointCutId(String path, String method) {
		boolean check = !(path.matches(".*/complain/.*") || path.matches(".*/hide/.*"))
				&& "Put".equalsIgnoreCase(method);
		check = check || "Delete".equalsIgnoreCase(method);
		return check;
	}

//  check post method
	private boolean checkPointCutLogin(String path, String method) {
		boolean check = "Post".equalsIgnoreCase(method);
		return check;
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
