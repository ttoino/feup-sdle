# FEUP-SDLE

Group project for the SDLE course unit at FEUP.

This project involved developing a distributed shopping list application with conflict-free replicated data types (CRDTs) for eventual consistency across multiple clients.

## Architecture

The system consists of two main components:

### Client

A web-based client application that allows users to:
- Create and manage shopping lists
- Add, edit, and remove items
- Synchronize data across devices
- Work offline with eventual consistency

### Server

A backend server that:
- Handles client connections via WebSockets
- Manages data replication between clients
- Provides persistent storage
- Implements CRDT merge logic

## Running

The project uses Docker Compose to orchestrate the services:

```bash
docker-compose up
```

This will start both the client and server components.

### Development

For development, you can run the components separately:

**Server:**
```bash
cd server
# Follow server-specific instructions
```

**Client:**
```bash
cd client
# Follow client-specific instructions
```

## Technologies

- **Client**: Web application (check client directory for specific tech stack)
- **Server**: Check server directory for implementation details
- **Networking**: WebSockets for real-time communication
- **Consistency**: CRDTs for conflict resolution

## Unit info

- **Name**: Sistemas Distribuídos de Larga Escala (Large Scale Distributed Systems)
- **Date**: Year 1, Semester 1, 2023/24
- [**More info**](https://sigarra.up.pt/feup/ucurr_geral.ficha_uc_view?pv_ocorrencia_id=518808)

## Disclaimer

This repository (and all others with the name format `feup-*`) are for archival and educational purposes only.

If you don't understand some part of the code or anything else in this repo, feel free to ask (although I may not understand it myself anymore).

Keep in mind that this repo is public. If you copy any code and use it in your school projects you may be flagged for plagiarism by automated tools.
