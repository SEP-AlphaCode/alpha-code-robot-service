package com.alpha_code.alpha_code_robot_service.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceResponse implements Serializable {
    public boolean success;
    public String message;
}
