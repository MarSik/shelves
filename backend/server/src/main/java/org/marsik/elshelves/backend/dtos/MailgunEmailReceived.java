package org.marsik.elshelves.backend.dtos;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.marsik.elshelves.backend.app.mvc.ParamName;

import javax.validation.constraints.NotNull;

public class MailgunEmailReceived {
    @ParamName("message-id")
    String messageId;

    @ParamName("in-reply-to")
    String inReplyTo;

    @NotEmpty
    String recipient;

    @NotNull
    String sender;

    @NotNull
    String from;

    @NotNull
    String subject;

    @NotNull
    @ParamName("body-plain")
    String bodyPlain;

    @ParamName("stripped-text")
    String strippedText;

    @ParamName("stripped-signature")
    String strippedSignature;

    @ParamName("body-html")
    @SafeHtml
    String bodyHtml;

    @ParamName("stripped-html")
    @SafeHtml
    String strippedHtml;

    @ParamName("attachment-count")
    String attachmentCount;

    Integer timestamp;

    @ParamName("message-headers")
    String messageHeaders;

    @ParamName("content-id-map")
    String contentIdMap;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyPlain() {
        return bodyPlain;
    }

    public void setBodyPlain(String bodyPlain) {
        this.bodyPlain = bodyPlain;
    }

    public String getStrippedText() {
        return strippedText;
    }

    public void setStrippedText(String strippedText) {
        this.strippedText = strippedText;
    }

    public String getStrippedSignature() {
        return strippedSignature;
    }

    public void setStrippedSignature(String strippedSignature) {
        this.strippedSignature = strippedSignature;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getStrippedHtml() {
        return strippedHtml;
    }

    public void setStrippedHtml(String strippedHtml) {
        this.strippedHtml = strippedHtml;
    }

    public String getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(String attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageHeaders() {
        return messageHeaders;
    }

    public void setMessageHeaders(String messageHeaders) {
        this.messageHeaders = messageHeaders;
    }

    public String getContentIdMap() {
        return contentIdMap;
    }

    public void setContentIdMap(String contentIdMap) {
        this.contentIdMap = contentIdMap;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }
}
