package com.mobon.frame.persistence;

import com.mobon.frame.model.FrameCycleStatsPdct;
import com.mobon.frame.model.FrameCycleStatsRankDto;
import com.mobon.frame.model.FrameCycleStatsTargetDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface FrameMapper {

    public List<FrameCycleStatsTargetDto> getFrameCycleStatsTarget();

    public List<FrameCycleStatsRankDto> getFrameCycleStatsRank(FrameCycleStatsTargetDto frameTarget);

    void saveFrameCyclePdct(FrameCycleStatsPdct frameCycleStatsPdct);

    public List<FrameCycleStatsRankDto> getFrameCycleStatsRankAll();

    public int saveFrameCyclePdctAll();
}
