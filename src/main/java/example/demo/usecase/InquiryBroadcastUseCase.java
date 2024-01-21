package example.demo.usecase;


import example.demo.dto.InquirySseMessage;

public interface InquiryBroadcastUseCase {
    void broadcast(InquirySseMessage messageObject);
}
