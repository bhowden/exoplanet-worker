# Exoplanet Worker

`exoplanet-worker` is a Spring Boot application designed to efficiently process and update information on exoplanets. The application retrieves all exoplanets from a Redis database and processes them in chunks. Each chunk is asynchronously updated by fetching the latest data from a separate service through an SSH connection. The updated data is then persisted back to Redis.

## Features

- **Chunk Processing**: To handle the vast number of exoplanets (around 2400), the application divides them into manageable chunks and processes each chunk simultaneously.
  
- **Asynchronous Updates**: Each chunk is processed asynchronously to maximize performance and minimize the total time taken.
  
- **SSH Communication**: The application communicates with the `exoplanet-finder` service over SSH. This service is a combination of Java and C, providing the latest data on exoplanets.

## Getting Started

### Prerequisites

- Docker installed on your machine.
  
### Building the Docker Image

From the root directory of the project:

```bash
docker build -t exoplanet-worker .
```

### Running the Application with Docker

After building the Docker image, you can run the application:

```bash
docker run -p 8080:8080 exoplanet-worker
```

This will start the application and expose it on port `8080` of your machine.
