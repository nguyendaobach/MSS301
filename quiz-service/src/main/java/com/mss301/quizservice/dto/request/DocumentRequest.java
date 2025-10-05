package com.mss301.quizservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentRequest {
    String name;
    String description;
    String category;
    Timestamp createdDate;
    String url;
    Double price;

}
