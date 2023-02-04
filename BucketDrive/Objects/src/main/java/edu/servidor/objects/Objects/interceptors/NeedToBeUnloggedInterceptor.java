package edu.servidor.objects.Objects.interceptors;

import edu.servidor.objects.Objects.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class NeedToBeUnloggedInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        if (user != null){
            response.sendRedirect("/objects");
            return false;
        }
        return true;
    }
}
