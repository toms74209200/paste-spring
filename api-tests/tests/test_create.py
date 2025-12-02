import requests

from lib.api_config import BASE_URL


def test_post_pastes_normal():
    response = requests.post(
        f"{BASE_URL}/pastes",
        json={
            "content": "console.log('Hello World');",
            "title": "Test Paste",
            "language": "javascript",
        },
    )
    assert response.status_code == 201
    assert isinstance(response.json()["id"], str)


def test_post_pastes_without_content_then_bad_request():
    response = requests.post(
        f"{BASE_URL}/pastes",
        json={
            "title": "No Content",
        },
    )
    assert response.status_code == 400


def test_post_pastes_with_invalid_expires_in_then_bad_request():
    response = requests.post(
        f"{BASE_URL}/pastes",
        json={
            "content": "Valid content",
            "expires_in": "invalid",
        },
    )
    assert response.status_code == 400
