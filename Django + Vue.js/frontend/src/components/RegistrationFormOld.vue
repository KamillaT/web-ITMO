<template>
  <div class="registration-form">
    <h2 v-if="isRegisterMode">Register</h2>
    <h2 v-else>Login</h2>
    <form @submit.prevent="submitForm">
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
      isRegisterMode: true, // Toggle between Register and Login
      user: {
        email: '',
        password: '',
        username: '', // Only for registration
      },
      errorMessage: '',
    };
  },
  methods: {
    async submitForm() {
      try {
        if (this.isRegisterMode) {
          // Register the user
          await api.registerUser(this.user);
        }

        // Automatically log in after registration or handle login
        const loginResponse = await api.loginUser({
          email: this.user.email,
          password: this.user.password,
        });

        // Emit the login event to parent with user details
        this.$emit('user-logged-in', loginResponse.data);

        // Reset the form
        this.resetForm();
      } catch (error) {
        this.errorMessage =
            error.response?.data?.error || 'Something went wrong.';
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
