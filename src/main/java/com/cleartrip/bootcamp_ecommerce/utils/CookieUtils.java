package com.cleartrip.bootcamp_ecommerce.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class CookieUtils {
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Don't create a new session if not present

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
}
