package com.example.demo.controller;


import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BoardController {

    private final BoardService boardService;
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


    @GetMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> getBoard(@PathVariable Long boardId){

        Map<String,Object> pages = boardService.findById(boardId);
        if(pages !=null){
            return ResponseEntity.ok(pages);
        }
        else{
            return ResponseEntity.notFound().build();
        }



    }
}
