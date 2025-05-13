package org.example.event_project.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsService {
    // TODO: Replace with your Twilio credentials
    public static final String ACCOUNT_SID = "AC2df08dcb6bddec0c659344b97d40f2d1";
    public static final String AUTH_TOKEN = "c65324756f3ebb7ff0bee2869dca29da";
    public static final String FROM_NUMBER = "+18382321676"; // Your Twilio number

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void sendSms(String to, String body) {
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(FROM_NUMBER),
                body
        ).create();
        System.out.println("SMS sent: " + message.getSid());
    }
} 