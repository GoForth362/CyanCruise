package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CareerLoopDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CosmicAssistantChatStorage implements AssistantChatStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicAssistantChatStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public AssistantChatSessionDto saveSession(AssistantChatSessionDto session) {
        CosmicDatamodelRecord row = session.getSessionId() == null ? null
                : gateway.load(CareerLoopDatamodelObjects.ASSISTANT_SESSION, session.getSessionId());
        if (row == null) {
            row = new CosmicDatamodelRecord(CareerLoopDatamodelObjects.ASSISTANT_SESSION)
                    .set(CareerLoopDatamodelObjects.CREATED_AT, session.getCreatedAt() == null ? LocalDateTime.now() : session.getCreatedAt());
        }
        row.set(CareerLoopDatamodelObjects.USER_ID, session.getUserId())
                .set("title", session.getTitle())
                .set("model_name", session.getModelName())
                .set("persona", session.getPersona())
                .set(CareerLoopDatamodelObjects.UPDATED_AT, session.getUpdatedAt() == null ? LocalDateTime.now() : session.getUpdatedAt());
        return toSession(gateway.save(row));
    }

    public AssistantChatSessionDto loadSession(Long sessionId) {
        CosmicDatamodelRecord row = gateway.load(CareerLoopDatamodelObjects.ASSISTANT_SESSION, sessionId);
        if (row == null) {
            throw new IllegalArgumentException("assistant session not found: " + sessionId);
        }
        return toSession(row);
    }

    public List<AssistantChatSessionDto> listSessions(final String userId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CareerLoopDatamodelObjects.ASSISTANT_SESSION, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CareerLoopDatamodelObjects.USER_ID));
            }
        }, DatamodelFieldMapper.dateTimeDesc(CareerLoopDatamodelObjects.UPDATED_AT));
        List<AssistantChatSessionDto> result = new ArrayList<AssistantChatSessionDto>();
        for (CosmicDatamodelRecord row : rows) {
            result.add(toSession(row));
        }
        return result;
    }

    public void deleteSession(Long sessionId) {
        gateway.delete(CareerLoopDatamodelObjects.ASSISTANT_SESSION, sessionId);
        deleteMessages(sessionId);
    }

    public AssistantChatMessageDto saveMessage(AssistantChatMessageDto message) {
        CosmicDatamodelRecord row = message.getMsgId() == null ? null
                : gateway.load(CareerLoopDatamodelObjects.ASSISTANT_MESSAGE, message.getMsgId());
        if (row == null) {
            row = new CosmicDatamodelRecord(CareerLoopDatamodelObjects.ASSISTANT_MESSAGE);
        }
        row.set("session_id", message.getSessionId())
                .set("role", message.getRole())
                .set("content", message.getContent())
                .set("prompt_tokens", message.getPromptTokens())
                .set("completion_tokens", message.getCompletionTokens())
                .set("total_tokens", message.getTotalTokens())
                .set("cost_micros", message.getCostMicros())
                .set(CareerLoopDatamodelObjects.CREATED_AT, message.getCreatedAt() == null ? LocalDateTime.now() : message.getCreatedAt());
        return toMessage(gateway.save(row));
    }

    public List<AssistantChatMessageDto> listMessages(final Long sessionId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CareerLoopDatamodelObjects.ASSISTANT_MESSAGE, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(sessionId, record.get("session_id"));
            }
        }, createdAtAsc());
        List<AssistantChatMessageDto> result = new ArrayList<AssistantChatMessageDto>();
        for (CosmicDatamodelRecord row : rows) {
            result.add(toMessage(row));
        }
        return result;
    }

    public void deleteMessages(final Long sessionId) {
        gateway.deleteWhere(CareerLoopDatamodelObjects.ASSISTANT_MESSAGE, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(sessionId, record.get("session_id"));
            }
        });
    }

    private AssistantChatSessionDto toSession(CosmicDatamodelRecord row) {
        AssistantChatSessionDto dto = new AssistantChatSessionDto();
        dto.setSessionId(DatamodelFieldMapper.asLong(row.get(CareerLoopDatamodelObjects.ID)));
        dto.setUserId(DatamodelFieldMapper.asString(row.get(CareerLoopDatamodelObjects.USER_ID)));
        dto.setTitle(DatamodelFieldMapper.asString(row.get("title")));
        dto.setModelName(DatamodelFieldMapper.asString(row.get("model_name")));
        dto.setPersona(DatamodelFieldMapper.asString(row.get("persona")));
        dto.setCreatedAt(DatamodelFieldMapper.asDateTime(row.get(CareerLoopDatamodelObjects.CREATED_AT)));
        dto.setUpdatedAt(DatamodelFieldMapper.asDateTime(row.get(CareerLoopDatamodelObjects.UPDATED_AT)));
        return dto;
    }

    private AssistantChatMessageDto toMessage(CosmicDatamodelRecord row) {
        AssistantChatMessageDto dto = new AssistantChatMessageDto();
        dto.setMsgId(DatamodelFieldMapper.asLong(row.get(CareerLoopDatamodelObjects.ID)));
        dto.setSessionId(DatamodelFieldMapper.asLong(row.get("session_id")));
        dto.setRole(DatamodelFieldMapper.asString(row.get("role")));
        dto.setContent(DatamodelFieldMapper.asString(row.get("content")));
        dto.setPromptTokens(DatamodelFieldMapper.asInteger(row.get("prompt_tokens")));
        dto.setCompletionTokens(DatamodelFieldMapper.asInteger(row.get("completion_tokens")));
        dto.setTotalTokens(DatamodelFieldMapper.asInteger(row.get("total_tokens")));
        dto.setCostMicros(DatamodelFieldMapper.asLong(row.get("cost_micros")));
        dto.setCreatedAt(DatamodelFieldMapper.asDateTime(row.get(CareerLoopDatamodelObjects.CREATED_AT)));
        return dto;
    }

    private Comparator<CosmicDatamodelRecord> createdAtAsc() {
        return new Comparator<CosmicDatamodelRecord>() {
            public int compare(CosmicDatamodelRecord left, CosmicDatamodelRecord right) {
                LocalDateTime leftValue = DatamodelFieldMapper.asDateTime(left.get(CareerLoopDatamodelObjects.CREATED_AT));
                LocalDateTime rightValue = DatamodelFieldMapper.asDateTime(right.get(CareerLoopDatamodelObjects.CREATED_AT));
                if (leftValue == null && rightValue == null) {
                    return 0;
                }
                if (leftValue == null) {
                    return -1;
                }
                if (rightValue == null) {
                    return 1;
                }
                return leftValue.compareTo(rightValue);
            }
        };
    }
}
