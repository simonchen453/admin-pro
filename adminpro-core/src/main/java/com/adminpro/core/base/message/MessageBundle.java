package com.adminpro.core.base.message;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author simon
 */
public class MessageBundle {

    private Map<String, Message> infoMap = new HashMap<>();
    private Map<String, Message> warningMap = new HashMap<>();
    private Map<String, Message> errorMap = new HashMap<>();
    private List<String> globalMessage = new ArrayList<>();

    private void addMessage(Message message) {
        getTypedMessage(message.getType()).put(message.getField(), message);
    }

    private Map<String, Message> getTypedMessage(String type) {
        if (Message.TYPE_ERROR.equals(type)) {
            return errorMap;

        } else if (Message.TYPE_WARNING.equals(type)) {
            return warningMap;

        } else {
            return infoMap;
        }
    }

    public void addErrorMessage(String field, String message) {
        addMessage(new Message(Message.TYPE_ERROR, field, message));
    }

    public void addInfoMessage(String field, String message) {
        addMessage(new Message(Message.TYPE_INFO, field, message));
    }

    public void addWarningMessage(String field, String message) {
        addMessage(new Message(Message.TYPE_WARNING, field, message));
    }

    public void addGlobalMessage(String message) {
        globalMessage.add(message);
    }

    public Message[] getErrorMessages() {
        return getMessagesByType(Message.TYPE_ERROR);
    }

    public Message[] getInfoMessages() {
        return getMessagesByType(Message.TYPE_INFO);
    }

    public Message[] getWarningMessages() {
        return getMessagesByType(Message.TYPE_WARNING);
    }

    public Message getErrorMessage(String field) {
        return getMessage(Message.TYPE_ERROR, field);
    }

    public Message getInfoMessage(String field) {
        return getMessage(Message.TYPE_INFO, field);
    }

    public Message getWarningMessage(String field) {
        return getMessage(Message.TYPE_WARNING, field);
    }

    private Message[] getMessagesByType(String messageType) {
        Assert.notNull(messageType, "Message Type cannot be null.");

        ArrayList<Message> returnList = new ArrayList<Message>();
        Map<String, Message> typedMsg = getTypedMessage(messageType);
        for (Entry<String, Message> entry : typedMsg.entrySet()) {
            returnList.add(entry.getValue());
        }

        return returnList.toArray(new Message[returnList.size()]);

    }

    public Message[] getMessages(String field) {
        field = regulateField(field);

        ArrayList<Message> returnList = new ArrayList<Message>();
        for (Entry<String, Message> entry : infoMap.entrySet()) {
            if (StringUtils.equals(field, entry.getKey())) {
                returnList.add(entry.getValue());
            }
        }
        for (Entry<String, Message> entry : warningMap.entrySet()) {
            if (StringUtils.equals(field, entry.getKey())) {
                returnList.add(entry.getValue());
            }
        }
        for (Entry<String, Message> entry : errorMap.entrySet()) {
            if (StringUtils.equals(field, entry.getKey())) {
                returnList.add(entry.getValue());
            }
        }

        return returnList.toArray(new Message[returnList.size()]);
    }

    public List<String> getGlobalMessage() {
        return globalMessage;
    }

    private Message getMessage(String messageType, String field) {
        field = regulateField(field);

        return getTypedMessage(messageType).get(field);
    }

    public boolean hasErrorMessage() {
        return hasMessage(Message.TYPE_ERROR);
    }

    public boolean hasInfoMessage() {
        return hasMessage(Message.TYPE_INFO);
    }

    public boolean hasWarningMessage() {
        return hasMessage(Message.TYPE_WARNING);
    }

    public boolean hasInfoMessage(String field) {
        return hasMessage(Message.TYPE_INFO, field);
    }

    public boolean hasWarningMessage(String field) {
        return hasMessage(Message.TYPE_WARNING, field);
    }

    public boolean hasGlobalMessage() {
        return !this.globalMessage.isEmpty();
    }

    public boolean hasErrorMessage(String field) {
        return hasMessage(Message.TYPE_ERROR, field);
    }

    private boolean hasMessage(String messageType, String field) {
        return null != getMessage(messageType, field);
    }

    private boolean hasMessage(String messageType) {
        return MapUtils.isNotEmpty(getTypedMessage(messageType));
    }

    private static String regulateField(String field) {
        return StringUtils.trimToEmpty(field);
    }
}
