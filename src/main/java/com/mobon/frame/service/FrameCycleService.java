package com.mobon.frame.service;

import com.mobon.frame.model.FrameCycleStatsPdct;
import com.mobon.frame.model.FrameCycleStatsRankDto;
import com.mobon.frame.model.FrameCycleStatsTargetDto;

import java.util.List;

public interface FrameCycleService {

    void frameCycleStatsPdctScheduler();

    void frameCycleRankPdctProcess2();

    int saveFrameCycleRankPdctAllProcess();

    List<FrameCycleStatsTargetDto> getFrameCycleStatsTarget();

    List<FrameCycleStatsRankDto> getFrameCycleStatsRank(FrameCycleStatsTargetDto frameTarget);

    void saveFrameCyclePdct(List<FrameCycleStatsPdct> frameCycleStatsPdctList);

    List<FrameCycleStatsRankDto> getFrameCycleStatsRankAll();
}
