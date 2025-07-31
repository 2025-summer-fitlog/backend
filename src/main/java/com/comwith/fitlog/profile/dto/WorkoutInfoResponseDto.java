package com.comwith.fitlog.profile.dto;

import com.comwith.fitlog.init.dto.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutInfoResponseDto {
    private PhysicalInfoRequest physicalInfo;
    private List<String> goals;
    private PreferredTimeRequest preferredTime;
    private WorkoutFrequencyRequest frequency;
    private List<String> mainParts;

}
