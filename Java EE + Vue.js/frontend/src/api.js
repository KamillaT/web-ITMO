const API_URL = 'http://localhost:8080/backend/api/';

export const api = {
  registerUser(userData) {
    return fetch(`${API_URL}users/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify(userData),
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((errorData) => {
            const error = new Error(errorData.message || 'Failed to register user.');
            error.response = { status: response.status, data: errorData };
            throw error;
          });
        }
        return response.json();
      });
  },
  loginUser(userData) {
    return fetch(`${API_URL}users/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify(userData),
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((errorData) => {
            const error = new Error(errorData.message || 'Failed to log in.');
            error.response = { status: response.status, data: errorData };
            throw error;
          });
        }
        return response.json();
      });
  },
  getPersonalPage() {
    const authToken = localStorage.getItem('authToken');
    return fetch(`${API_URL}users/personal`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((errorData) => {
            const error = new Error(errorData.message || 'Failed to fetch personal page.');
            error.response = { status: response.status, data: errorData };
            throw error;
          });
        }
        return response.json();
      });
  },
  submitPoint(pointData) {
    const authToken = localStorage.getItem('authToken');
    return fetch(`${API_URL}points/checkpoint`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(pointData),
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((errorData) => {
            const error = new Error(errorData.message || 'Failed to submit point.');
            error.response = { status: response.status, data: errorData };
            throw error;
          });
        }
        return response.json();
      });
  },
  getPoints(authToken) {
    return fetch(`${API_URL}points`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((errorData) => {
            const error = new Error(errorData.message || 'Failed to fetch point.');
            error.response = { status: response.status, data: errorData };
            throw error;
          });
        }
        return response.json();
      });
  },
  clearPoints(authToken) {
  return fetch(`${API_URL}points/clear`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  }).then((response) => {
    if (!response.ok) {
      return response.json().then((errorData) => {
        const error = new Error(errorData.message || 'Failed to delete points.');
        error.response = { status: response.status, data: errorData };
        throw error;
      });
    }
    return response.status;
  });
  },
};

