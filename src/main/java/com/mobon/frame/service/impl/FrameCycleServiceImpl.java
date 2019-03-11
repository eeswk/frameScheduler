package com.mobon.frame.service.impl;

import com.mobon.frame.model.FrameCycleStatsPdct;
import com.mobon.frame.model.FrameCycleStatsRankDto;
import com.mobon.frame.model.FrameCycleStatsTargetDto;
import com.mobon.frame.persistence.FrameMapper;
import com.mobon.frame.service.FrameCycleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class FrameCycleServiceImpl implements FrameCycleService {

    @Autowired
    private FrameMapper frameMapper;

    /**
     * 스케쥴러 시작 단계
     */
    @Override
    @Transactional
    public void frameCycleStatsPdctScheduler() {
        // 대상자 리스트가져오기
        List<FrameCycleStatsTargetDto> frameCycleTarget =  getFrameCycleStatsTarget();
        if(frameCycleTarget.size() > 0) {
            frameCycleTarget.stream().forEach(f -> {
                frameCycleRankPdctProcess(f);
            });
        }
        log.info("target size = {} ", frameCycleTarget.size());
    }

    private void frameCycleRankPdctProcess(FrameCycleStatsTargetDto target) {
        FrameCycleStatsTargetDto frameTarget =  target ;
        //프레임별 순위
        List<FrameCycleStatsRankDto> frameCycleStatsRankList = getFrameCycleStatsRank(frameTarget);
//        log.info("frameCycleStatsRankList size = {} ", frameCycleStatsRankList.size());
        //프레임별 예측추출
        List<FrameCycleStatsPdct> frameCycleStatsPdctList = frameCycleRankCompareByBeforeCycleNum(frameCycleStatsRankList);
        //저장하기
        saveFrameCyclePdct(frameCycleStatsPdctList);
    }

    private List<FrameCycleStatsPdct> frameCycleRankCompareByBeforeCycleNum(List<FrameCycleStatsRankDto> frameCycleRankList) {

        //정렬
        frameCycleRankList.stream().sorted((v1, v2) -> {
            return v1.getCycleTrn() - v2.getCycleTrn();
        });

//        frameCycleRankList.forEach(System.out::println);
        //사이클별 그룹핑
        Map<Integer, List<FrameCycleStatsRankDto>> framCycleTrnsGroupBy = frameCycleRankList.stream()
                .collect(groupingBy(f -> f.getCycleTrn()));

//        log.info("framCycleTrnsGroupBy = {}, size = {}", framCycleTrnsGroupBy, framCycleTrnsGroupBy.size());
//        log.info("framCycleTrnsGroupBy size = {}", framCycleTrnsGroupBy.size());

        List<FrameCycleStatsPdct> frameCycleStatsPdctList = new ArrayList<>();
        List<FrameCycleStatsRankDto> preRankList = null;

        for(Integer key : framCycleTrnsGroupBy.keySet()) {
            List<FrameCycleStatsRankDto> list = framCycleTrnsGroupBy.get(key);

            if (preRankList != null) {
                List<FrameCycleStatsRankDto> finalPreRankList = preRankList;
                //파티셔닝
                /*
                list.stream()
                        .collect(Collectors.partitioningBy(l -> hasSameFrameCodeSet(finalPreRankList).contains(l.getFrameCode())))
                        .computeIfPresent(true, (aBoolean, frameCycleStatsRankDtos) -> frameCycleStatsPdctStream(frameCycleStatsRankDtos,aBoolean ))

                        .get(true).stream().flatMap(p -> frameCycleStatsPdctStream(p, "Y"))
                        .get(false).stream().flatMap(p -> frameCycleStatsPdctStream(p, "N")).collect(Collectors.toList());

                */
                Map<Boolean, List<FrameCycleStatsRankDto>> partitionList = list.stream()
                        .collect(Collectors.partitioningBy(l -> hasSameFrameCodeSet(finalPreRankList).contains(l.getFrameCode())));

                List<FrameCycleStatsPdct> frameCycleStatsPdctTrueList= partitionList.get(true).stream().flatMap(p -> frameCycleStatsPdctStream(p, "Y")).collect(Collectors.toList());
                List<FrameCycleStatsPdct> frameCycleStatsPdctFalseList= partitionList.get(false).stream().flatMap(p -> frameCycleStatsPdctStream(p, "N")).collect(Collectors.toList());

                frameCycleStatsPdctList.addAll(frameCycleStatsPdctTrueList);
                frameCycleStatsPdctList.addAll(frameCycleStatsPdctFalseList);
            } else{
                //첫번째 사이클도 예측실패로 디폴트 넣기
                List<FrameCycleStatsPdct> frameCycleStatsPdctFalseList = list.stream().flatMap(p -> frameCycleStatsPdctStream(p, "N")).collect(Collectors.toList());
                frameCycleStatsPdctList.addAll(frameCycleStatsPdctFalseList);
            }
            preRankList = list;
        }
        log.info("frameCycleStatsPdctList size = {}", frameCycleStatsPdctList.size());
        //log.info("frameCycleStatsPdctList = {}, size = {}", frameCycleStatsPdctList, frameCycleStatsPdctList.size());
        return frameCycleStatsPdctList;
    }

    @Transactional
    public void frameCycleRankPdctProcess2() {
        //대상자 가져오기
        List<FrameCycleStatsRankDto> frameCycleStatsRankAll = getFrameCycleStatsRankAll();

        Map<String, List<FrameCycleStatsRankDto>> collect = new HashMap<>();
        for (FrameCycleStatsRankDto f : frameCycleStatsRankAll) {
            collect.computeIfAbsent(customerKey(f), k -> new ArrayList<>()).add(f);
        };

        for(String key : collect.keySet()) {
            List<FrameCycleStatsRankDto> list = collect.get(key);
            List<FrameCycleStatsPdct>  frameCycleStatsPdctList = frameCycleRankCompareByBeforeCycleNum(list);
            //저장하기
            saveFrameCyclePdct(frameCycleStatsPdctList);
        }
    }

    @Transactional
    public int saveFrameCycleRankPdctAllProcess(){
        return frameMapper.saveFrameCyclePdctAll();
    }

    public String customerKey(FrameCycleStatsRankDto frame) {
        return frame.getMediaScriptNo() + "-" + frame.getAlgmSeq() + "-" + frame.getPrdtTpCode();
    }



    public List<FrameCycleStatsRankDto> getFrameCycleStatsRankAll() {
        return frameMapper.getFrameCycleStatsRankAll();
    }

    //프레임 사이클 타겟 리스트
    @Override
    public List<FrameCycleStatsTargetDto> getFrameCycleStatsTarget() {
        return frameMapper.getFrameCycleStatsTarget();
    }

    //사이클별 순위추출
    @Override
    public List<FrameCycleStatsRankDto> getFrameCycleStatsRank(FrameCycleStatsTargetDto frameTarget) {
        return frameMapper.getFrameCycleStatsRank(frameTarget);
    }


    private FrameCycleStatsPdct newframeCodeCycleState(FrameCycleStatsRankDto frameCycleStatsRankDto, String successYn) {
        return new FrameCycleStatsPdct().builder()
                .mediaScriptNo(frameCycleStatsRankDto.getMediaScriptNo())
                .algmSeq(frameCycleStatsRankDto.getAlgmSeq())
                .prdtTpCode(frameCycleStatsRankDto.getPrdtTpCode())
                .frameCode(frameCycleStatsRankDto.getFrameCode())
                .cycleTrn(frameCycleStatsRankDto.getCycleTrn())
                .pdctSuccessYn(successYn).build();
    }

    public Stream<FrameCycleStatsPdct> frameCycleStatsPdctStream(FrameCycleStatsRankDto frameCycleStatsRankDto, String successYn) {
        List<FrameCycleStatsPdct> result = new ArrayList<>();
        result.add(newframeCodeCycleState(frameCycleStatsRankDto, successYn));
        return result.stream();
    }


    private Set<String> hasSameFrameCodeSet(List<FrameCycleStatsRankDto> preRankList) {
        return preRankList.stream()
                .map(FrameCycleStatsRankDto::getFrameCode)
                .collect(Collectors.toSet());
    }

    @Override
    public void saveFrameCyclePdct(List<FrameCycleStatsPdct> frameCycleStatsPdctList) {
        for (FrameCycleStatsPdct frameCycleStatsPdct : frameCycleStatsPdctList)
            frameMapper.saveFrameCyclePdct(frameCycleStatsPdct);
    }
}
