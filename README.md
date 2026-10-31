# Vagas Extractor - PLATAFORMA DE COLETA E ANÁLISE DE VAGAS DE EMPREGO PARA APOIO À TOMADA DE DECISÃO PROFISSIONAL

Plataforma que coleta vagas de TI em portais de emprego brasileiros, extrai as habilidades exigidas, armazena em um banco de dados e analisa quais tecnologias o mercado mais demanda.

## Como executar

### Pré-requisitos

- Java 21 e Maven (ou o wrapper do projeto)
- Docker e Docker Compose
- Node.js 18 ou superior (para o frontend)

### Variáveis de ambiente

| Variável | Para que serve |
|---|---|
| `JWT_SECRET` | Chave de assinatura dos tokens. Obrigatória em produção |
| `MAILTRAP_USER` e `MAILTRAP_PASS` | Credenciais SMTP para envio de e-mails |
| Credenciais do banco | Usuário, senha e nome do banco do PostgreSQL |

Os arquivos `.env` não ficam no repositório., crie-os localmente.

### Backend

```bash
# 1. Subir o banco
docker compose up -d

# 2. Executar a aplicação
cd back
./mvnw spring-boot:run
```

A API responde na porta `8080`.

### Frontend

Crie o arquivo `front/.env` com a URL da API:

```
VITE_API_URL=http://localhost:8080
```

Depois:

```bash
cd front
npm install
npm run dev
```

### Execução completa com Docker

```bash
docker compose up -d --build
```
