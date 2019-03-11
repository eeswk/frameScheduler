package com.mobon.frame.model;

import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;


@Data
@ToString
@Alias("frameCycleStatsTargetDto")
public class FrameCycleStatsTargetDto {

    private int mediaScriptNo;
    private int algmSeq;
    private String prdtTpCode;
}


