package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import unzip_util.UnzipFile;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class DownloadAttachment {
    private static Logger logger = Logger.getLogger(DownloadAttachment.class.getSimpleName());
    private static final Set<String> wrongSubjectAddresses = new HashSet<>();
    private static final Set<String> zipFilePaths = new HashSet<>();
    private static final String SAVE_DIR =  "D:/save_location/";
    public static void getAttachment(String host, String username, String password) {
        try {
            Store store = connect(host, username, password);
            //create the folder object and open INBOX in Gmail
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            logger.log(Level.INFO, "messages number {0}", messages.length);
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                String mailSubject = message.getSubject();
                String address ="";
                
                // get mail which has  their subject starts with "ITLAB-HOMEWORK"
                if (Pattern.matches("ITLAB-HOMEWORK.*", mailSubject)) {
                    // get address
                    Address[] addresses = message.getFrom();
                    address = addresses == null ? null : ((InternetAddress) addresses[0]).getAddress();
                    System.out.println("---------------------------------");
                    System.out.println("Email # " + (i + 1));
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("From: " + address);
                    System.out.println("Text: " + message.getContentType());
                    System.out.println();

                    // get attachments
                    Multipart multipart = (Multipart) message.getContent();
                    for (int j = 0; j < multipart.getCount(); j++) {
                        MimeBodyPart bodyPart = (MimeBodyPart)multipart.getBodyPart(j);
                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {

                            String fileName = bodyPart.getFileName();
                            String fileExtension = fileName.substring(fileName.lastIndexOf('.')+1);
                            if (fileExtension.equalsIgnoreCase("zip")) {
                            	logger.log(Level.INFO, "Find a file: " + fileName);
                            	System.out.println();
                                String outputFilePath = SAVE_DIR + address + "-" + fileName;
                                bodyPart.saveFile(outputFilePath);
                                UnzipFile.unzipFileHandler(outputFilePath);
                                addZipFilePath(outputFilePath);

                            } else {
                            	addWrongSubjectAddress(address);
                            }
                        }
                    }                   
                } else{
                    addWrongSubjectAddress(address);
                }
            }
            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Store connect(String host, String username, String password) 
    		throws MessagingException {
        // configure properties
        Properties properties = new Properties();

        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        
        Session emailSession = Session.getDefaultInstance(properties);

        //create the IMAP store object and connect with the imap server
        Store store = emailSession.getStore("imaps");
        // connect
        store.connect(host, username, password);
        return store;
    }
    
    private static void addWrongSubjectAddress(String address) {
        synchronized (wrongSubjectAddresses) {
            wrongSubjectAddresses.add(address);
        }
    }    
    public static Set<String> getAllWrongSubjectAddresses() {
        synchronized (wrongSubjectAddresses) {
            if (wrongSubjectAddresses.isEmpty())
                return Collections.emptySet();
            else {
                HashSet<String> wrongAddresses = new HashSet<>(wrongSubjectAddresses);
                wrongSubjectAddresses.clear();
                return wrongAddresses;
            }
        }
    }

    private static void addZipFilePath(String filePath) {
        synchronized (zipFilePaths) {
            zipFilePaths.add(filePath);
        }
    }

    public static Set<String> getAllZipFilePaths() {
        synchronized (zipFilePaths) {
            if (zipFilePaths.isEmpty())
                return Collections.emptySet();
            else {
                HashSet<String> paths = new HashSet<>(zipFilePaths);
                zipFilePaths.clear();
                return paths;
            }
        }
    }

    public static void main(String[] args) {
        logger = Logger.getLogger(DownloadAttachment.class.getSimpleName());
        
        String host = "imap.gmail.com";// change accordingly
        String username = "thanhhoang123d@gmail.com";// change accordingly
        String pasword = "shadowhunter97";// change accordingly

        getAttachment(host, username, pasword);
    }
}