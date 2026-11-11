package filter;

import model.Customer8;
import model.Manager8;
import model.Member8;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter kiểm tra quyền truy cập và phân quyền phiên làm việc (Session)
 * cho các khu vực /customer/* và /manager/*.
 */
@WebFilter(filterName = "AuthenticationFilter8", urlPatterns = {"/customer/*", "/manager/*"})
public class AuthenticationFilter8 implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        HttpSession session = req.getSession(false);
        Member8 member = (session != null) ? (Member8) session.getAttribute("member") : null;

        if (member == null) {
            // Chưa đăng nhập
            res.sendRedirect(contextPath + "/member/LoginView8.jsp?error=login_required");
            return;
        }

        if (path.startsWith("/customer/")) {
            if (!(member instanceof Customer8)) {
                // Đăng nhập nhưng không có vai trò khách hàng
                res.sendRedirect(contextPath + "/member/LoginView8.jsp?error=customer_access_denied");
                return;
            }
        } else if (path.startsWith("/manager/")) {
            if (!(member instanceof Manager8)) {
                // Đăng nhập nhưng không có vai trò nhân viên quản lý
                res.sendRedirect(contextPath + "/member/LoginView8.jsp?error=manager_access_denied");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

