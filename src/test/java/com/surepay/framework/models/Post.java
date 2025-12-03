package com.surepay.framework.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("userId")
    private Integer userId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("body")
    private String body;

    public boolean belongsToUser(Integer targetUserId) {
        return userId != null && userId.equals(targetUserId);
    }
        public boolean isValidPost() {
        return id != null && 
               id > 0 && 
               userId != null && 
               userId > 0 &&
               title != null && 
               !title.trim().isEmpty() &&
               body != null && 
               !body.trim().isEmpty();
    }
}
