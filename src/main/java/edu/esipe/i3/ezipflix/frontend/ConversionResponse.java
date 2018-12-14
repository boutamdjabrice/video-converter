package edu.esipe.i3.ezipflix.frontend;

import java.util.UUID;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
public class ConversionResponse {

    private String uuid;
    private String messageId;
    private String dbOutcome;

    public ConversionResponse() {
    }

    public ConversionResponse(String uuid, String messageId, String dbOutcome) {
        this.uuid = uuid;
        this.messageId = messageId;
        this.dbOutcome = dbOutcome;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDbOutcome() {
        return dbOutcome;
    }

    public void setDbOutcome(String dbOutcome) {
        this.dbOutcome = dbOutcome;
    }
}
