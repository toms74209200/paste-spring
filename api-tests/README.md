# Paste API Tests

## Environments

- Python
- [uv](https://docs.astral.sh/uv/) 0.9.13

## Setup

```bash
uv sync
```

Generate client code from OpenAPI spec.

```bash
uv run openapi-python-client generate --path ../spec.yml  --output-path openapi_gen --overwrite
```

## Usage

```bash
uv run pytest tests/ -vv
```
