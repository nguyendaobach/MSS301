package mss.mindmap.mindmapservice.mindmap.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PremiumResponse {

    int id;

    String userId;

    boolean isPremium;

    LocalDateTime time;
}
