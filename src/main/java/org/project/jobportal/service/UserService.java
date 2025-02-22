package org.project.jobportal.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.project.jobportal.dto.LoginDTO;
import org.project.jobportal.dto.NotificationDTO;
import org.project.jobportal.dto.ResponseDTO;
import org.project.jobportal.dto.UserDTO;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.model.OTP;
import org.project.jobportal.model.User;
import org.project.jobportal.repository.NotificationRepo;
import org.project.jobportal.repository.OTPRepository;
import org.project.jobportal.repository.UserRepository;
import org.project.jobportal.utils.Data;
import org.project.jobportal.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private OTPRepository otpRepository;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private NotificationService notificationService;

    public Boolean sendOtp(String email) throws Exception {
        User user=userRepository.findByEmail(email).orElseThrow(() -> new JobPortalException("USER_NOT_FOUND"));
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Your OTP Code");
        String genOtp = Utilities.generateOtp();
        OTP otp=new OTP(email,genOtp, LocalDateTime.now());
        otpRepository.save(otp);
        mimeMessageHelper.setText(Data.getMessageBody(genOtp,user.getName()),true);
        mailSender.send(mimeMessage);
        return true;
    }

    public UserDTO register(UserDTO userDTO) throws JobPortalException {
        Optional<User> optional = userRepository.findByEmail(userDTO.getEmail());
        if(optional.isPresent()) {
            throw new JobPortalException("USER_FOUND");
        }
        userDTO.setProfileId(profileService.createProfile(userDTO.getEmail()));
        userDTO.setId(Utilities.getNextSequence("users"));
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = userDTO.toEntity();
        user=userRepository.save(user);
        return user.toDTO();
    }

    public UserDTO getUserByEmail(String email) throws JobPortalException {
        return userRepository.findByEmail(email).orElseThrow(()->new JobPortalException("User Not Found")).toDTO();
    }

    public UserDTO login(LoginDTO loginDTO) throws JobPortalException {
        User user=userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new JobPortalException("USER_NOT_FOUND"));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new JobPortalException("INVALID_CREDENTIALS");
        }
        return user.toDTO();
    }

    public Boolean verifyOtp(String email, String otp) throws JobPortalException{
        OTP otpEntity = otpRepository.findById(email).orElseThrow(() -> new JobPortalException("OTP_NOT_FOUND"));
        if (!otpEntity.getOtpCode().equals(otp)){
            throw new JobPortalException("INVALID_OTP");
        }
        return true;
    }

    public ResponseDTO forgotPassword(LoginDTO loginDTO) throws JobPortalException {
        User user=userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new JobPortalException("USER_NOT_FOUND"));
        user.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
        userRepository.save(user);
        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(user.getId());
        notification.setMessage("Password Reset Successfull");
        notification.setAction("Password Reset");
        notificationService.sendNotification(notification);
        return new ResponseDTO("Password Changed Successfully");
    }

    @Scheduled(fixedRate = 60000)
    public void removeExpiredOTP(){
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(5);
        List<OTP> expiredOTP = otpRepository.findByCreationTimeBefore(expiry);
        if(!expiredOTP.isEmpty()){
            otpRepository.deleteAll(expiredOTP);
            System.out.println("Removed "+expiredOTP.size()+" expired OTPs");
        }
    }
}
