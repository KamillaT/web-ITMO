import axios from 'axios';

const API_URL = 'http://localhost:8000/api/users/';

const apiClient = axios.create({
    baseURL: API_URL,
});

// apiClient.interceptors.response.use(
//     (response) => response,
//     (error) => {
//         if (error.response && error.response.status === 401) {
//             localStorage.removeItem('authToken');
//             window.location.href = '/';
//         }
//         return Promise.reject(error);
//     }
// );

export const api = {
    registerUser(userData) {
        return apiClient.post('register/', userData);
    },
    loginUser(userData) {
        return apiClient.post('token/', userData);
    },
    getPersonalPage() {
        return apiClient.get('personal/', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('authToken')}`,
            },
        });
    },
    submitPoint(authToken, pointData) {
        return apiClient.post('check-point/', pointData, {
            headers: {
                Authorization: `Bearer ${authToken}`,
            },
        });
    },
    getPoints(authToken) {
        return apiClient.get('points/', {
            headers: {
                Authorization: `Bearer ${authToken}`,
            },
        });
    },
};


// export const api = {
//     registerUser(userData) {
//         return axios.post(`${API_URL}register/`, userData);
//     },
//     loginUser(userData) {
//         return axios.post(`${API_URL}login/`, userData);
//     },
//     getPersonalPage(email) {
//         return axios.get(`${API_URL}personal/`, {
//             params: { email: email },
//         });
//     },
// };
