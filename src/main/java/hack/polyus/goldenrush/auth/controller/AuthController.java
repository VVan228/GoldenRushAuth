package hack.polyus.goldenrush.auth.controller;

import hack.polyus.goldenrush.auth.jwt.JwTokenProvider;
import hack.polyus.goldenrush.auth.model.User;
import hack.polyus.goldenrush.auth.requests.AuthRequest;
import hack.polyus.goldenrush.auth.requests.UpdateTokenRequest;
import hack.polyus.goldenrush.auth.service.UserRepo;
import hack.polyus.goldenrush.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @ResponseBody
    @RequestMapping(
            value="/auth/login",
            method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody AuthRequest authRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            User user = userService.getByEmail(authRequest.getEmail());

            return updateTokens(user);

        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid email/password combination");
            return ResponseEntity.badRequest().body(response);
        }
    }


    @ResponseBody
    @RequestMapping(
            value="/auth/updateAccessToken",
            method = RequestMethod.POST
    )
    public ResponseEntity<Map<String, String>> updateToken(@RequestBody UpdateTokenRequest updateTokenRequest){

        System.out.println("update token request");

        String refreshToken = updateTokenRequest.getRefresh_token();

        boolean isValid = jwtTokenProvider.validateToken(refreshToken);
        String email = jwtTokenProvider.getEmail(refreshToken);
        User user = userService.getByEmail(email);

        boolean isLast = passwordEncoder.matches(refreshToken,user.getCurrentRefreshTokenHash());

        if(isLast && isValid){
            return updateTokens(user);
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("message", "invalid token");
            return ResponseEntity.badRequest().body(response);
        }

    }


    private ResponseEntity<Map<String, String>> updateTokens(User user){
        String access_token = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name(), user.getId());
        String refresh_token = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRole().name());

        userService.replaceRefreshToken(user, passwordEncoder.encode(refresh_token));

        Map<String, String> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("access_token", access_token);
        response.put("refresh_token", refresh_token);
        return ResponseEntity.ok(response);
    }

}
