package edu.servidor.objects.Objects.interceptors;

import edu.servidor.objects.Objects.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class NeededToHavePermission implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        //TODO obtener dueño y usuarios con permisos de acceso al objeto o al bucket y si coincide con "currentUser" permitir acceso
        if (user.getUsername().equals(request.getParameter(""))){
            response.sendRedirect("/objects");
            return false;
        }
        return true;
    }
}
