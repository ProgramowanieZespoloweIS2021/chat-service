package chatservice.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum MessageStatus {
    SENT("SENT"),
    READ("READ");

    private final String code;
    MessageStatus(String code){
        this.code = code;
    }

    @JsonCreator
    public static MessageStatus decode(final String code) {
        return Stream.of(MessageStatus.values()).filter(targetEnum -> targetEnum.code.equals(code)).findFirst().orElse(SENT);
    }

    @JsonValue
    public String getMessageStatus() {
        return code;
    }
}
