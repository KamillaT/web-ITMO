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
        <button type="submit">{{ isRegisterMode ? 'Register' : 'Login' }}</button>
      </div>
    </form>
    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div>
      <button @click="toggleMode">
        {{ isRegisterMode ? 'Already have an account? Login' : "Don't have an account? Register" }}
      </button>
    </div>
  </div>
</template>

<script>
import { api } from '../api';

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
    async submitForm() {
      try {
        if (this.isRegisterMode) {
          await api.registerUser(this.user);
        }

        const loginResponse = await api.loginUser({
          email: this.user.email,
          password: this.user.password,
        });

        localStorage.setItem('authToken', loginResponse.data.access);
        console.log(loginResponse.data.access, localStorage.getItem('authToken'));

        this.$emit('user-logged-in', loginResponse.data);

        this.resetForm();
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.errorMessage = 'Invalid email or password. Please try again.';
        } else {
          this.errorMessage = 'An error occurred. Please try again later.';
        }
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
  background-color: #28a745;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #218838;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>
