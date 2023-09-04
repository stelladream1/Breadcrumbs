# [팀 과제] 노션에서 브로드 크럼스(Breadcrumbs) 만들기

## 목표

---

노션과 유사한 간단한 페이지 관리 API를 구현해주세요. 각 페이지는 제목, 컨텐츠, 그리고 서브 페이지를 가질 수 있습니다. 또한, 특정 페이지에 대한 브로드 크럼스(Breadcrumbs) 정보도 반환해야 합니다.

## 요구사항

---

**페이지 정보 조회 API**: 특정 페이지의 정보를 조회할 수 있는 API를 구현하세요.

- 입력: 페이지 ID
- 출력: 페이지 제목, 컨텐츠, 서브 페이지 리스트, **브로드 크럼스 ( 페이지 1 > 페이지 3 > 페이지 5)**
- **** 컨텐츠 내에서 서브페이지 위치 고려  X*

## 제출 방법 (팀단위)

---

- **과제 내용을 노션 혹은 github 등에 문서화해서 제출해주세요. (마감 9월 5일 오전 10시)**
- **필수**
    - 테이블 구조
    - 비지니스 로직 (Raw 쿼리로 구현 → ORM (X))
    - 결과  정보
        
        ```java
        {
        		"pageId" : 1,
        		"title" : 1,
        		"subPages" : [],
        		"breadcrumbs" : ["A", "B", "C",] // 혹은 "breadcrumbs" : "A / B / C"
        }
        ```
        
- 제출하신 과제에 대해서 설명해주세요. (”왜 이 구조가 최선인지?” 등)

<hr>
## 테이블 구조
![스크린샷 2023-09-04 185439](https://github.com/stelladream1/Breadcrumbs/assets/74993171/d8dfa9e7-0dcf-417b-82a7-8619e13a84ac)                  
 
하위 페이지를 찾기 위해서는 현재 페이지의 상위페이지를 저장해야된다고 생각했습니다.  그렇기 때문에 top_page 라는 컬럼을 만들었습니다. 
pageName은 브로드크럼스를 출력할 때 페이지의 아이디(board_id)가 아닌 페이지의 이름을(ex. 홈, 카테고리1 , 카테고리2 등) 리턴하기 위해 만들었습니다.  


## 비즈니스 로직

1. 요청한 페이지의 아이디, 제목, 내용을 가져오는 쿼리
```
String mainPageQuery = "SELECT b.board_id, b.board_title, b.board_content " +
                       "FROM board b " +
                       "WHERE b.board_id = ?";
```

2. 요청한 페이지의 하위 페이지 리스트를 가져오는 쿼리
```
String subPagesQuery = "SELECT b.board_id, b.board_title , b.board_content " +
                       "FROM board b " +
                       "WHERE b.top_page_id = ?";

```
3. 요청한 페이지의 브로드크럼스를 가져오는 쿼리

이것을 바탕으로 맨 상위 페이지갈 때 까지 반복해서 현재 페이지의 상위페이지를 계속 찾아갑니다. 
이것을 배열로 저장한 결과의 reverse 를 저장합니다. 

```
String pageNameQuery = "SELECT b.pageName " +
                       "FROM board b " +
                       "WHERE b.board_id = ?";
```


## 결과 정보

현재 데이터 베이스에 저장된 페이지 정보                  
         

GET http://localhost:8080/1
```

{
    "board_id": 1,
    "board_title": "test1",
    "board_content": "test1",
    "subPages": [
        {
            "board_id": 2,
            "board_title": "test2",
            "board_content": "test2"
        },
        {
            "board_id": 4,
            "board_title": "test4",
            "board_content": "test4"
        }
    ],
    "breadcrumbs": "[페이지 1]"
}
```

GET http://localhost:8080/3
```
{
    "board_id": 3,
    "board_title": "test3",
    "board_content": "test3",
    "subPages": [],
    "breadcrumbs": "[페이지 1,페이지 2,페이지 3]"
}
```
