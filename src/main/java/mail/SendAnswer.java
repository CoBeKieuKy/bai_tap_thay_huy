package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendAnswer {
    public static void main(String[] args) {
        // Recipient's email ID needs to be mentioned.
        String to = "hoangcongthanh1237d@gmail.com";

        // Sender's email ID needs to be mentioned
        String from = "thanhhoang123d@gmail.com";
        
        final String username = "thanhhoang123d@gmail.com";//change accordingly
        final String password = "shadowhunter97";//change accordingly
        sendEmail(from,password,to);
    }

    public static void sendEmail(String username, String password, String to) {
        String host = "smtp.gmail.com";
        Properties props = new Properties();
        
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(username));

            // Set To: header field of the header.
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("From Adviser with LOVE <3");

            // Now set the actual message
            message.setText("Nothing gonna change my love for you !");

            // Send message
            Transport.send(message);

            System.out.println("Sending completed !");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}