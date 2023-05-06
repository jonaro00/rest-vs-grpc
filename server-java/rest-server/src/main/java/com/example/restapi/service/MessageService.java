package com.example.restapi.service;

import com.example.restapi.api.model.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private List<Message> messageList;

    public MessageService(){
        messageList = new ArrayList<>();

        Message message1 = new Message(1, "My first test message");
        Message message2 = new Message(2, "My second message");
        Message message3 = new Message(3, "Hello World!");
        Message message4 = new Message(4, "Hello, My name is Daniel");
        Message message5 = new Message(5, "Hello! \n My name is Daniel. \n Who are you?");

        messageList.addAll(Arrays.asList(message1,message2,message3,message4,message5));
    }

    public Optional<Message> getMessage(Integer id){
        Optional optional = Optional.empty();
        for (Message message: messageList) {
            if (id == message.getId()) {
                optional = Optional.of(message);
                return optional;
            }
        }
        return optional;
    }
}
