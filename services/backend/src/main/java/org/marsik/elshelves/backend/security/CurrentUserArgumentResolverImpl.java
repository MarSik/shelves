package org.marsik.elshelves.backend.security;

import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

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

        if (principal == null) {
            return null;
        }

        UserRepository userRepository =
                applicationContext.getBean("userRepository", UserRepository.class);

        User user = userRepository.getUserByEmail(principal.getName());
        return user;
    }
}