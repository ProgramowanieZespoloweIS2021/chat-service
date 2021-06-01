[![CI/CD](https://github.com/ProgramowanieZespoloweIS2021/chat-service/actions/workflows/ci.yml/badge.svg)](https://github.com/ProgramowanieZespoloweIS2021/chat-service/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/ProgramowanieZespoloweIS2021/chat-service/branch/main/graph/badge.svg?token=8ZTZXEZT6F)](https://codecov.io/gh/ProgramowanieZespoloweIS2021/chat-service)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/ProgramowanieZespoloweIS2021/chat-service)](https://github.com/ProgramowanieZespoloweIS2021/chat-service/releases)
[![Docker Image Version (latest by date)](https://img.shields.io/docker/v/arokasprz100/chat-service?label=dockerhub%20image)](https://hub.docker.com/r/arokasprz100/chat-service)

## For developers
Following actions are currently supported:
* creating chat room (for two or more users)
* getting user status
* sending messages in selected chat room
* receiving chat notifications
* retrieving chat messages for given chat room
* retrieving chat rooms for given user
* marking messages as read

### Creating chat room
URL: `localhost:8080/chats/createRoom` (POST)

In this request users will be registered in chat service database.

Body example:
```json
[
        {"id": 3, "nickname": "Mati"},
        {"id": 2, "nickname": "Seba"}
]
```
ID should be the same as user IDs in other services

Response example:
```json
{
    "id": 1,
    "users": [
        {
            "id": 3,
            "nickname": "Mati",
            "lastActivity": null
        },
        {
            "id": 2,
            "nickname": "Seba",
            "lastActivity": null
        }
    ],
    "lastActivityDate": "2021-05-27T19:08:38.228+00:00"
}
```
### Checking user info
URL: `localhost:8080/user/{USER_ID}` (GET)

URL Example: `localhost:8080/user/2`

Response body example
```json
{
    "id": 2,
    "nickname": "Seba",
    "lastActivity": "2021-05-27T18:34:19.330+00:00"
}
```


### Retrieving most recent chat list for user with optional paging settings
Default page size is 15 and offset is set to 0.

URL: `localhost:8080/chats/{USER_ID}?pageOffset=0&pageSize=15` (GET)

URL Example: `localhost:8080/chats/2`

Response body example
```json
[
  {
    "id": 1,
    "users": [
      {
        "id": 3,
        "nickname": "Mati",
        "lastActivity": "2021-05-27T20:50:18.256+00:00"
      },
      {
        "id": 2,
        "nickname": "Seba",
        "lastActivity": "2021-05-27T19:58:54.239+00:00"
      }
    ],
    "lastActivityDate": "2021-05-27T19:58:55.039+00:00"
  },
  {
    "id": 2,
    "users": [
      {
        "id": 3,
        "nickname": "Mati",
        "lastActivity": "2021-05-27T20:50:18.256+00:00"
      },
      {
        "id": 4,
        "nickname": "Janusz",
        "lastActivity": null
      }
    ],
    "lastActivityDate": "2021-05-27T20:50:09.571+00:00"
  }
]
```

### Retrieving most recent chat messages with optional paging settings
Default page size is 15 and offset is set to 0.

URL: `localhost:8080/messages/{CHAT_ID}?pageOffset=0&pageSize=15` (GET)

URL Example: `localhost:8080/messages/2`

Response body example:
```json
[
    {
        "id": 1,
        "chatId": 1,
        "content": "lalalala",
        "timestamp": "2021-06-01T16:48:40.032+00:00",
        "messageStatus": "SENT",
        "senderId": 4,
        "recipientsIds": null
    },
    {
        "id": 2,
        "chatId": 1,
        "content": "lalalala",
        "timestamp": "2021-06-01T16:48:40.032+00:00",
        "messageStatus": "SENT",
        "senderId": 4,
        "recipientsIds": null
    }
]
```


### Marking messages as read by user
URL: `/chats/{chatID}/readByUser/{userID}` (PUT)

URL Example: `/chats/1/readByuser/3`

Request body example: (should be taken from the last message in selected chat)
```json
{"id":1, "senderId":2, "messageStatus":"SENT"}
```
Those fields above are required. (the order may be different)

Response body example:
```json
{
    "id": 1,
    "chatId": 1,
    "content": "lalalala",
    "timestamp": "2021-06-01T16:48:40.032+00:00",
    "messageStatus": "READ",
    "senderId": 4,
    "recipientsIds": null
}
```

### Chat via websockets with STOMP

WS connection URL: `ws://localhost:8080/ws`

User can listen for incoming notifications on dedicated channel (should be subscribed): `/user/{USER_ID}/queue/messages`

User can send messages on channel `/app/chat`

Example of message payload:
```json
{"timestamp":1622566120032, "senderId":3, "content":"lalalala", "chatId":1}
```
Those fields above are required. (the order may be different)

#### Possible notifications:
* New message arrived

Body of that notification:
```json
{"chatId":1,"user":{"id":4,"nickname":"Janusz","lastActivity":1622566560165},"messageId":2,"notificationType":"NEW_MESSAGE_FROM"}
```
* Message in chat was read by any recipient

Body of that notification:
```json
{"chatId":1,"user":{"id":3,"nickname":"Mati","lastActivity":1622566525284},"messageId":1,"notificationType":"USER_READ_CHAT_MESSAGE"}
```

### Additional comments
Functionality was tested using Postman and http://jxy.me/websocket-debug-tool/ (for websockets)