import requests
from openapi_gen.paste_api_client import Client
from openapi_gen.paste_api_client.api.create import post_pastes
from openapi_gen.paste_api_client.models.post_pastes_body import PostPastesBody

from lib.api_config import BASE_URL

api_client = Client(base_url=BASE_URL)


def test_get_pastes_json_normal():
    body = PostPastesBody(content="Test content for reading", title="Read Test", language="python")
    post_response = post_pastes.sync_detailed(client=api_client, body=body)
    paste_id = post_response.parsed.id

    get_response = requests.get(f"{BASE_URL}/pastes/{paste_id}.json")
    assert get_response.status_code == 200
    assert get_response.json()["content"] == "Test content for reading"


def test_get_pastes_raw_normal():
    body = PostPastesBody(content="Raw text content")
    post_response = post_pastes.sync_detailed(client=api_client, body=body)
    paste_id = post_response.parsed.id

    response = requests.get(f"{BASE_URL}/pastes/{paste_id}/raw")
    assert response.status_code == 200
    assert response.text == "Raw text content"


def test_get_pastes_html_normal():
    body = PostPastesBody(content="HTML content")
    post_response = post_pastes.sync_detailed(client=api_client, body=body)
    paste_id = post_response.parsed.id

    response = requests.get(f"{BASE_URL}/pastes/{paste_id}.html")
    assert response.status_code == 200


def test_get_pastes_json_not_found():
    response = requests.get(f"{BASE_URL}/pastes/nonexistent.json")
    assert response.status_code == 404


def test_get_pastes_raw_not_found():
    response = requests.get(f"{BASE_URL}/pastes/nonexistent/raw")
    assert response.status_code == 404


def test_get_pastes_html_not_found():
    response = requests.get(f"{BASE_URL}/pastes/nonexistent.html")
    assert response.status_code == 404
