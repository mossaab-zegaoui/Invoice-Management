package com.example.securebusiness.event;

import com.example.securebusiness.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class onRegistrationCompleteEvent extends ApplicationEvent {
    private User user;
    private Locale locale;
    private String appUrl;

    public onRegistrationCompleteEvent(User user, Locale locale, String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
