package io.github.carlinhoshk.APITV.controller;

import io.github.carlinhoshk.APITV.model.user.AuthenticationDTO;
import io.github.carlinhoshk.APITV.model.user.LoginResponseDTO;
import io.github.carlinhoshk.APITV.model.user.RegisterDTO;
import io.github.carlinhoshk.APITV.model.user.UserModel;
import io.github.carlinhoshk.APITV.repository.UserRepository;
import io.github.carlinhoshk.APITV.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((UserModel) auth.getPrincipal());
        var userId = ((UserModel) auth.getPrincipal()).getId();

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping(value = "/username")
    public String username(@RequestBody @Valid AuthenticationDTO data) {
        var user = this.repository.findByLogin(data.login());
        return user.getUsername();
    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if (this.repository.findByLogin(data.login()) != null)
            return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        UserModel newUser = new UserModel(data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);
        return ResponseEntity.ok().build();
        }
}
