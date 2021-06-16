# wkbe-cg-be
# Project Overview
The search engine is a java application that is hoested in Apache webserver opened on port 8080. It has intereactions with Solr to expose and facilitates searches/predictions mechanisms needed by [wkbe-cg-fe](https://github.com/thetwit4u/wkbe-cg-fe). It exposes some rest endpoints to interact with the search engine.
# Available Scripts
Compiling the project
``` mvn clean install ```

Running the project using the executable jar under the target folder
``` java -jar searcherengine-0.0.1-SNAPSHOT.jar ```

Running the project using maven
``` mvn spring-boot:run ```

# Endpoints
- /topics
The endpoint accepts two request parameters


| Param Name | Param Type |
| -------- | -------- |
| Topic     | String     |
| limit     | int     |

eg. endpoint : http://searchengine.rickandmorty-team.com:8080/topics?topic=Stichting in moeilijkheden&limit=2

```
[
    {
        "id": "421",
        "label": "Legaat aan private stichting",
        "path": "Legaat / Legaat aan private stichting"
    },
    {
        "id": "478",
        "label": "Stichting Gellingen",
        "path": "Stichting / Stichting Gellingen"
    }
]
```

- /topics
The endpoint accepts one parameter

| Param Name | Param Type |
| -------- | -------- |
| Topics     | List of topic Ids |

eg.http://searchengine.rickandmorty-team.com:8080/topics?topics=1,417

```
[
    {
        "id": "417",
        "label": "Belastingvrije som voor kind ten laste",
        "path": "Belastingvrije som / Belastingvrije som voor kind ten laste"
    },
    {
        "id": "1",
        "label": "Sociale zekerheid",
        "path": "Sociale zekerheid"
    }
]
```


- docs/summary
The endpoint accepts one parameter

| Param Name | Param Type |
| -------- | -------- |
| Topics     | List of String |

eg. http://searchengine.rickandmorty-team.com:8080/docs/summary?topics=1009,1
```
[
    {
        "ids": [
            "1"
        ],
        "count": 64
    },
    {
        "ids": [
            "1009",
            "1"
        ],
        "count": 1
    },
    {
        "ids": [
            "1009"
        ],
        "count": 67
    }
]
```

- /docs
The endpoint accepts one parameter

| Param Name | Param Type |
| -------- | -------- |
| Topics     | List of String |

eg.http://searchengine.rickandmorty-team.com:8080/docs?topics=1929,471
```
[
    {
        "id": "60c8609376120bd203274475",
        "title": "et deserunt eiusmod velit id aliqua ad do in",
        "topics": [
            471,
            1159
        ]
    },
    {
        "id": "60c860931ef9d38bdb26d99c",
        "title": "enim ex sint cillum esse Lorem tempor",
        "topics": [
            1929,
            1205,
            1057
        ]
    }
]
```

- /docs/{id}

| Param Name | Param Type |
| -------- | -------- |
| id     | String |

eg.http://searchengine.rickandmorty-team.com:8080/docs/60c86093501325217bdad0e2
```
[
    {
        "id": "60c86093501325217bdad0e2",
        "title": "id dolor tempor labore pariatur nisi pariatur magna aute dolore aliqua pariatur adipisicing ut",
        "topics": [
            214,
            95,
            1273,
            1997
        ]
    }
]
```
- /docs/suggest
The endpoint accepts one parameter and return the suggested topic that have the most occurences in intersections with the given topics. A negative value will be returned in case there were no suggestions.

| Param Name | Param Type |
| -------- | -------- |
| Topics     | List of String |

eg. http://searchengine.rickandmorty-team.com:8080/docs/suggest?topics=214,955

```
1714
```
