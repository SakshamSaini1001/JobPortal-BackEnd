package org.project.jobportal.utils;

public class Data {
    public static String getMessageBody(String otp,String userName){
        return  "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
                "    <title>OTP Verification</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                "        .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px;" +
                "                          box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                "        .email-header { text-align: center; padding-bottom: 20px; border-bottom: 1px solid #eaeaea; }" +
                "        .email-header h1 { margin: 0; font-size: 24px; color: #333333; }" +
                "        .email-body { padding: 20px; text-align: center; }" +
                "        .email-body p { font-size: 16px; line-height: 1.5; color: #555555; }" +
                "        .otp-code { display: inline-block; font-size: 32px; font-weight: bold; color: #ffffff; background-color: #007bff;" +
                "                     padding: 10px 20px; margin-top: 20px; letter-spacing: 3px; border-radius: 5px; }" +
                "        .email-footer { text-align: center; padding-top: 20px; font-size: 14px; color: #777777; }" +
                "        .email-footer p { margin: 5px 0; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"email-container\">" +
                "        <!-- Email Header -->" +
                "        <div class=\"email-header\">" +
                "            <h1>OTP Verification</h1>" +
                "        </div>" +
                "        <!-- Email Body -->" +
                "        <div class=\"email-body\">" +
                "            <p>Hello " + userName + ",</p>" +
                "            <p>To proceed with your action, please use the following OTP (One-Time Password):</p>" +
                "            <div class=\"otp-code\">" + otp + "</div>" +
                "            <p>This OTP is valid for the next 10 minutes.</p>" +
                "            <p>If you didn't request this, please ignore this email or contact support.</p>" +
                "        </div>" +
                "        <!-- Email Footer -->" +
                "        <div class=\"email-footer\">" +
                "            <p>Thank you for using our services.</p>" +
                "            <p>If you have any questions, feel free to <a href=\"mailto:support@example.com\">contact us</a>.</p>" +
                "            <p>&copy; 2024 Job Portal. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
