package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;

@Service
public class BoardService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BoardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getPageNameById(Long boardId) {
        String pageNameQuery = "SELECT b.pageName " +
                "FROM board b " +
                "WHERE b.board_id = ?";
        return jdbcTemplate.queryForObject(pageNameQuery, String.class, boardId);
    }
    public Map<String, Object> findById(Long boardId) {
        String mainPageQuery = "SELECT b.board_id, b.board_title, b.board_content " +
                "FROM board b " +
                "WHERE b.board_id = ?";

        String subPagesQuery = "SELECT b.board_id, b.board_title , b.board_content " +
                "FROM board b " +
                "WHERE b.top_page_id = ?";

        List<Object> breadcrumbs = new ArrayList<>();
        Long currentTopPageId = boardId;
        breadcrumbs.add(getPageNameById(currentTopPageId));

        while (true) {

            String breadPageQuery = "SELECT b.top_page_id " +
                    "FROM board b " +
                    "WHERE b.board_id = ?";


            Long topPageId = jdbcTemplate.queryForObject(breadPageQuery, Long.class, currentTopPageId);
            if (topPageId == 0) {
                break;
            }
            breadcrumbs.add(getPageNameById(topPageId));
            currentTopPageId = topPageId;
        }


        Map<String, Object> pages = jdbcTemplate.queryForMap(mainPageQuery, boardId);

        List<Map<String, Object>> subs = jdbcTemplate.queryForList(subPagesQuery, boardId);
        pages.put("subPages", subs);

        Collections.reverse(breadcrumbs);
        String breadcrumbsResult = "[" + collectionToCommaDelimitedString(breadcrumbs) + "]";
//        pages.put("breadcrumbs", breadcrumbs);
        pages.put("breadcrumbs", breadcrumbsResult);
        return pages;
    }
}
