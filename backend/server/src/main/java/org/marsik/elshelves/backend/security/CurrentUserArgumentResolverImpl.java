package org.marsik.elshelves.backend.security;

import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.repositories.AuthorizationRepository;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;
import java.util.UUID;

@Component
public class CurrentUserArgumentResolverImpl implements CurrentUserArgumentResolver {

    final ApplicationContext applicationContext;

    @Autowired
    public CurrentUserArgumentResolverImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Principal principal = webRequest.getUserPrincipal();

        if (principal == null
                || !(principal instanceof Authentication)) {
            return null;
        }

        Authentication authentication = (Authentication)principal;
        boolean mobileDevice = false;

        for (GrantedAuthority g: authentication.getAuthorities()) {
            if (g.getAuthority().equals("ROLE_MOBILE")) {
                mobileDevice = true;
                break;
            }
        }

        if (mobileDevice) {
            AuthorizationRepository authRepository =
                    applicationContext.getBean("authorizationRepository", AuthorizationRepository.class);
            Authorization auth = authRepository.findById(UUID.fromString(principal.getName()));
            if (auth != null) {
                return auth.getOwner();
            }

            return null;
        } else {
            UserRepository userRepository =
                    applicationContext.getBean("userRepository", UserRepository.class);

            return userRepository.findById(UUID.fromString(principal.getName()));
        }
    }
}
