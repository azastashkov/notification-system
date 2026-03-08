# Notification System

A multi-channel notification delivery platform built with Spring Boot 3.4 and Java 21. Supports push notifications (APNs, FCM), SMS, and email delivery through an asynchronous, message-driven architecture.

## Architecture

The system consists of five modules:

| Module | Port | Description |
|---|---|---|
| **notification-server** | 8080 | REST API that accepts notification requests, validates them, renders templates, and publishes events to RabbitMQ |
| **notification-worker** | 8081 | Consumes messages from RabbitMQ queues and delivers notifications via third-party providers |
| **third-party-emulator** | 8082 | Simulates external delivery providers (APNs, FCM, SMS, email) for local development |
| **device-emulator** | 8083 | Simulates end-user devices receiving push notifications via WebSocket |
| **load-client** | — | Command-line load testing tool that seeds test data and sends bulk notification requests |

### Flow

```
Client → notification-server → RabbitMQ → notification-worker → third-party-emulator → device-emulator
```

1. Client sends a `POST /api/notifications` request
2. Server validates the request, resolves templates, persists the notification, and publishes an event to the appropriate RabbitMQ queue (`notification.apns`, `notification.fcm`, `notification.sms`, `notification.email`)
3. Worker consumes the event and calls the corresponding third-party provider
4. (In local dev) The third-party emulator forwards push notifications to the device emulator over HTTP

## Tech Stack

- **Java 21**, **Spring Boot 3.4**, **Gradle 8.10**
- **PostgreSQL** — user, device, template, and notification request storage
- **RabbitMQ** — asynchronous message delivery between server and workers
- **Redis** — caching (templates, user settings)
- **Micrometer + Prometheus** — metrics via `/actuator/prometheus`

## Prerequisites

- Java 21+
- PostgreSQL
- RabbitMQ
- Redis

## Getting Started

### Build

```bash
./gradlew build
```

### Run

Start infrastructure services (PostgreSQL, RabbitMQ, Redis), then launch each module:

```bash
# Terminal 1 - Device emulator
./gradlew :device-emulator:bootRun

# Terminal 2 - Third-party emulator
./gradlew :third-party-emulator:bootRun

# Terminal 3 - Notification server
./gradlew :notification-server:bootRun

# Terminal 4 - Notification worker
./gradlew :notification-worker:bootRun
```

### Configuration

All modules are configured via environment variables with sensible defaults for local development:

| Variable | Default | Used by |
|---|---|---|
| `DB_HOST` | `localhost` | server, worker |
| `DB_PORT` | `5432` | server, worker |
| `DB_NAME` | `notification_db` | server, worker |
| `DB_USER` | `postgres` | server, worker |
| `DB_PASSWORD` | `postgres` | server, worker |
| `RABBITMQ_HOST` | `localhost` | server, worker |
| `RABBITMQ_PORT` | `5672` | server, worker |
| `RABBITMQ_USER` | `guest` | server, worker |
| `RABBITMQ_PASSWORD` | `guest` | server, worker |
| `REDIS_HOST` | `localhost` | server |
| `REDIS_PORT` | `6379` | server |
| `THIRDPARTY_BASE_URL` | `http://localhost:8082` | worker |
| `DEVICE_EMULATOR_URL` | `http://localhost:8083` | third-party-emulator |

## API

### Send Notification

```
POST /api/notifications
```

Request body:

```json
{
  "userId": 1,
  "type": "EMAIL",
  "title": "Welcome",
  "body": "Hello, welcome to our platform!",
  "templateName": null,
  "templateParams": {}
}
```

Supported notification types: `APNS`, `FCM`, `SMS`, `EMAIL`.

### Load Testing

```bash
./gradlew :load-client:bootRun
```

Seeds test users, devices, and templates into the database, then sends a configurable burst of notification requests to the server.

## Testing

```bash
./gradlew test
```
