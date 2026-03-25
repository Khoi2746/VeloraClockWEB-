package com.example.asm1.config;

import com.example.asm1.Entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

@Override
public boolean preHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler) throws Exception {

    HttpSession session = request.getSession(false);
    User user = (session != null)
            ? (User) session.getAttribute("loggedInUser")
            : null;

 if (user == null || user.getRoleId() != 1) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
    return false;
}

    return true;
}
}
