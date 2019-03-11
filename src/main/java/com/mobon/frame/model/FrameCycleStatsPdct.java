package com.mobon.frame.model;

import lombok.*;
import org.apache.ibatis.type.Alias;


@Data
@ToString
@Alias("frameCycleStatsPdct")
@Setter
@Getter
public class FrameCycleStatsPdct {

    private int mediaScriptNo;
    private int algmSeq;
    private String prdtTpCode;
    private int cycleTrn;
    private String frameCode;
    private String pdctSuccessYn;

    @Builder
    public FrameCycleStatsPdct(int mediaScriptNo, int algmSeq, String prdtTpCode, int cycleTrn, String frameCode, String pdctSuccessYn) {
        this.mediaScriptNo = mediaScriptNo;
        this.algmSeq = algmSeq;
        this.prdtTpCode = prdtTpCode;
        this.frameCode = frameCode;
        this.cycleTrn = cycleTrn;
        this.pdctSuccessYn = pdctSuccessYn;
    }

    public FrameCycleStatsPdct() {
    }

    public FrameCycleStatsPdct(FrameCycleStatsRankDto frameCycleStatsRankDto) {
    }
}


