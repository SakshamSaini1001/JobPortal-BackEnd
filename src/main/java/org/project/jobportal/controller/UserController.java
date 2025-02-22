package org.project.jobportal.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.project.jobportal.dto.LoginDTO;
import org.project.jobportal.dto.ResponseDTO;
import org.project.jobportal.dto.UserDTO;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid UserDTO userDTO) throws JobPortalException {
        userDTO = userService.register(userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody @Valid LoginDTO loginDTO) throws JobPortalException {
        return new ResponseEntity<>(userService.login(loginDTO), HttpStatus.OK);
    }

    @PostMapping("/sendotp/{email}")
    public ResponseEntity<ResponseDTO> sendOtp(@PathVariable @Email(message = "{user.email.invalid}") String email) throws Exception {
        userService.sendOtp(email);
        return new ResponseEntity<>(new ResponseDTO("OTP Sent Successfully"), HttpStatus.OK);
    }

    @GetMapping("/sendotp/{email}/{otp}")
    public ResponseEntity<ResponseDTO> verifyOtp(@PathVariable @Email(message = "{user.email.invalid}") String email,@PathVariable @Pattern(regexp = "^[0-9]{6}$",message = "{otp.invalid}") String otp) throws JobPortalException {
        userService.verifyOtp(email,otp);
        return new ResponseEntity<>(new ResponseDTO("OTP Verified Successfully"), HttpStatus.OK);
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<ResponseDTO> forgotPassword(@RequestBody @Valid LoginDTO loginDTO) throws JobPortalException {
        return new ResponseEntity<>(userService.forgotPassword(loginDTO), HttpStatus.OK);
    }
}
