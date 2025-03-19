package com.cleartrip.bootcamp_ecommerce.utils;

import com.cleartrip.bootcamp_ecommerce.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class CookieUtils {
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            System.out.println("Session null");
            return false;
        } else if (session.getAttribute("userRole") == null) {
            System.out.println("Attribute null");
            return false;
        }
        String role = session.getAttribute("userRole").toString();
        return role.equals("Admin");
    }
    public static Long getUserId(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("Session null");
            throw new UnauthorizedAccessException("Login First");
        }
        return (Long) session.getAttribute("userId");
    }
}
