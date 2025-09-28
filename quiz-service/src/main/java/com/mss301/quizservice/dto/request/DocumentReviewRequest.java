package com.mss301.quizservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentReviewRequest {
    String documentId;
    int rating;
    String reviewer;
    String comment;
}
