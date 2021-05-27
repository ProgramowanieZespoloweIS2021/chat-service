[![CI/CD](https://github.com/ProgramowanieZespoloweIS2021/chat-service/actions/workflows/ci.yml/badge.svg)](https://github.com/ProgramowanieZespoloweIS2021/chat-service/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/ProgramowanieZespoloweIS2021/chat-service/branch/main/graph/badge.svg?token=8ZTZXEZT6F)](https://codecov.io/gh/ProgramowanieZespoloweIS2021/chat-service)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/ProgramowanieZespoloweIS2021/chat-service)](https://github.com/ProgramowanieZespoloweIS2021/chat-service/releases)
[![Docker Image Version (latest by date)](https://img.shields.io/docker/v/arokasprz100/chat-service?label=dockerhub%20image)](https://hub.docker.com/r/arokasprz100/chat-service)

##For developers
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
Will be updated soon