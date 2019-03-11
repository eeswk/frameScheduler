package com.mobon.frame.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;


@Data
@ToString
@Alias("frameCycleStatsRankDto")
@Getter
@Setter
public class FrameCycleStatsRankDto {

    private int mediaScriptNo;
    private int algmSeq;
    private String prdtTpCode;
    private String frameCode;
    private int cycleTrn;
    private double ctr;
    private int rank;

    public int getMediaScriptNo() {
        return mediaScriptNo;
    }

    public void setMediaScriptNo(int mediaScriptNo) {
        this.mediaScriptNo = mediaScriptNo;
    }

    public int getAlgmSeq() {
        return algmSeq;
    }

    public void setAlgmSeq(int algmSeq) {
        this.algmSeq = algmSeq;
    }

    public String getPrdtTpCode() {
        return prdtTpCode;
    }

    public void setPrdtTpCode(String prdtTpCode) {
        this.prdtTpCode = prdtTpCode;
    }

    public String getFrameCode() {
        return frameCode;
    }

    public void setFrameCode(String frameCode) {
        this.frameCode = frameCode;
    }

    public int getCycleTrn() {
        return cycleTrn;
    }

    public void setCycleTrn(int cycleTrn) {
        this.cycleTrn = cycleTrn;
    }

    public double getCtr() {
        return ctr;
    }

    public void setCtr(double ctr) {
        this.ctr = ctr;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}


