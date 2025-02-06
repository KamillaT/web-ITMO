<template>
  <div class="container registration-form">
    <h2 v-if="isRegisterMode">Register</h2>
    <h2 v-else>Login</h2>
    <form @submit.prevent="submitForm" class="form-container">
      <div>
        <input
            type="email"
            v-model="user.email"
            placeholder="Email"
            required
        />
      </div>
      <div>
        <input
            type="password"
            v-model="user.password"
            placeholder="Password"
            required
        />
      </div>
      <div v-if="isRegisterMode">
        <input
            type="text"
            v-model="user.username"
            placeholder="Username"
            required
        />
      </div>
      <div>
        <button class="btn" type="submit">{{ isRegisterMode ? 'Register' : 'Login' }}</button>
      </div>
    </form>
    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div>
      <button class="btn" @click="toggleMode">
        {{ isRegisterMode ? 'Already have an account? Login' : "Don't have an account? Register" }}
      </button>
    </div>
  </div>
  <div id="notification" class="notification" style="display: none;"></div>
</template>

<script>
import { api } from '../api';
import { handleApiError } from '../utils';

export default {
  name: 'RegistrationForm',
  data() {
    return {
      isRegisterMode: true,
      user: {
        email: '',
        password: '',
        username: '',
      },
      errorMessage: '',
    };
  },
  methods: {
    showNotification(message, imageUrl = null) {
      const notification = document.getElementById('notification');

      notification.innerHTML = '';

      if (imageUrl) {
        const img = document.createElement('img');
        img.src = imageUrl;
        img.alt = 'Notification Image';
        img.style.width = '150px';
        img.style.height = '150px';
        img.style.marginRight = '10px';
        img.style.verticalAlign = 'middle';
        notification.appendChild(img);
      }

      const text = document.createElement('span');
      text.textContent = message;
      console.log(message, imageUrl);
      notification.appendChild(text);

      notification.style.display = 'flex';
      notification.style.opacity = 1;

      setTimeout(() => {
        notification.style.opacity = 0;
        setTimeout(() => {
          notification.style.display = 'none';
        }, 500);
      }, 3000);
    },
    async submitForm() {
      try {
        if (this.isRegisterMode) {
          await api.registerUser(this.user);
        }

        const loginResponse = await api.loginUser({
          email: this.user.email,
          password: this.user.password,
        });

        localStorage.setItem('authToken', loginResponse.token);

        this.$emit('user-logged-in', loginResponse.data);

        this.resetForm();
      } catch (error) {
        handleApiError(error, this.showNotification);
      }
    },
    resetForm() {
      this.user.email = '';
      this.user.password = '';
      this.user.username = '';
    },
    toggleMode() {
      this.isRegisterMode = !this.isRegisterMode;
    },
  },
};
</script>

<style src="../assets/style/screenSize.css"></style>
<style scoped>
.registration-form {
  max-width: 400px;
  margin: 0 auto;
}

input {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
}

button {
  width: 100%;
  padding: 10px;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: rgba(255, 192, 203, 0.85);;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>
