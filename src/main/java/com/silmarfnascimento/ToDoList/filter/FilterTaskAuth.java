package com.silmarfnascimento.ToDoList.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.silmarfnascimento.ToDoList.user.IUserRepository;
import com.silmarfnascimento.ToDoList.user.UserModel;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    // recuperar o token de identificação
    var serveletPath = request.getServletPath();
    if (serveletPath.startsWith("/tasks")) {
      var authorizationEncoded = request.getHeader("Authorization").substring("Basic".length()).trim();

      // extrair as informações contidas no token
      byte[] authorizationDecoded = Base64.getDecoder().decode(authorizationEncoded);
      String[] credentials = new String(authorizationDecoded).split(":");
      String username = credentials[0];
      String password = credentials[1];

      // conferir se as credenciais estão contidas no banco de dado
      UserModel userFound = this.userRepository.findByUsername(username);
      if (userFound == null) {
        response.sendError(401);
      } else {
        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), userFound.getPassword());
        if (passwordVerify.verified) {
          request.setAttribute("idUser", userFound.getId());
          filterChain.doFilter(request, response);
        } else {
          response.sendError(401);
        }
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

}
