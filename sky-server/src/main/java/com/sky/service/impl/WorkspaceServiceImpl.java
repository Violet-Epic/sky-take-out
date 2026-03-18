package com.sky.service.impl;

import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final OrderDetailMapper orderDetailMapper;

    /**
     * 营业额统计
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = date.atStartOfDay();
            LocalDateTime endTime = date.atTime(LocalTime.MAX);
            Double turnover = orderMapper.sumAmountByDate(beginTime, endTime);
            turnoverList.add(turnover == null ? 0.0 : turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(joinList(dateList))
                .turnoverList(joinList(turnoverList))
                .build();
    }

    /**
     * 用户统计
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = date.atStartOfDay();
            LocalDateTime endTime = date.atTime(LocalTime.MAX);

            // 新增用户数
            Integer newUser = userMapper.countNewUserByDate(beginTime, endTime);
            newUserList.add(newUser == null ? 0 : newUser);

            // 截止当天的用户总量（第二天0点之前的用户数）
            LocalDateTime nextDayBegin = date.plusDays(1).atStartOfDay();
            Integer totalUser = userMapper.countTotalUserBeforeDate(nextDayBegin);
            totalUserList.add(totalUser == null ? 0 : totalUser);
        }

        return UserReportVO.builder()
                .dateList(joinList(dateList))
                .newUserList(joinList(newUserList))
                .totalUserList(joinList(totalUserList))
                .build();
    }

    /**
     * 订单统计
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        int totalOrderCount = 0;
        int totalValidOrderCount = 0;

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = date.atStartOfDay();
            LocalDateTime endTime = date.atTime(LocalTime.MAX);

            // 每日订单数
            Integer orderCount = orderMapper.countOrderByDate(beginTime, endTime);
            orderCount = orderCount == null ? 0 : orderCount;
            orderCountList.add(orderCount);
            totalOrderCount += orderCount;

            // 每日有效订单数
            Integer validCount = orderMapper.countValidOrderByDate(beginTime, endTime);
            validCount = validCount == null ? 0 : validCount;
            validOrderCountList.add(validCount);
            totalValidOrderCount += validCount;
        }

        // 订单完成率
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 :
                (double) totalValidOrderCount / totalOrderCount;

        return OrderReportVO.builder()
                .dateList(joinList(dateList))
                .orderCountList(joinList(orderCountList))
                .validOrderCountList(joinList(validOrderCountList))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalValidOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 销量排名Top10
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = begin.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        List<Map<String, Object>> salesList = orderDetailMapper.getSalesTop10(beginTime, endTime);

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        for (Map<String, Object> item : salesList) {
            nameList.add((String) item.get("name"));
            numberList.add(((Number) item.get("number")).intValue());
        }

        return SalesTop10ReportVO.builder()
                .nameList(String.join(",", nameList))
                .numberList(joinList(numberList))
                .build();
    }

    // ==================== 私有方法 ====================

    /**
     * 获取日期范围内每天的日期列表
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> list = new ArrayList<>();
        LocalDate current = begin;
        while (!current.isAfter(end)) {
            list.add(current);
            current = current.plusDays(1);
        }
        return list;
    }

    /**
     * 将列表拼接成逗号分隔的字符串
     */
    private <T> String joinList(List<T> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(list.get(i));
        }
        return sb.toString();
    }
}
