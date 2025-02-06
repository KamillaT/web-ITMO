<template>
  <div class="container personal-page">
    <h2>Your Personal Page</h2>
    <div v-if="user">
      <p><strong>Email:</strong> {{ user.email }}</p>
      <p><strong>Username:</strong> {{ user.username }}</p>
    </div>

    <h3>Graph</h3>
    <canvas id="graphCanvas" class="graph" width="800" height="800" @click="handleCanvasClick"></canvas>

    <h3>Add Points</h3>
    <form @submit.prevent="submitFormPoint" class="form-container">
      <div>
        <input
            type="number"
            v-model="point.x"
            placeholder="X"
            value="1.0"
            min="-5"
            max="5"
            step="0.1"
            required
        />
      </div>
      <div>
        <input
            type="number"
            v-model="point.y"
            placeholder="Y"
            value="1.0"
            min="-5"
            max="3"
            step="0.1"
            required
        />
      </div>
      <div>
        <input
            type="number"
            v-model="point.r"
            placeholder="R"
            value="1.0"
            min="1"
            max="5"
            step="0.1"
            required
            @input="draw"
        />
      </div>
      <div>
        <button type="submit">Check!</button>
      </div>
    </form>

    <h3>Your Points</h3>
    <table v-if="points.length">
      <thead>
      <tr>
        <th>X</th>
        <th>Y</th>
        <th>R</th>
        <th>Is Inside</th>
        <th>Execution Time</th>
        <th>Created At</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="point in points" :key="point.id">
        <td>{{ point.x }}</td>
        <td>{{ point.y }}</td>
        <td>{{ point.r }}</td>
        <td>{{ point.is_inside ? 'Yes' : 'No' }}</td>
        <td>{{ point.execution_time }} ms</td>
        <td>{{ new Date(point.created_at).toLocaleString() }}</td>
      </tr>
      </tbody>
    </table>
    <p v-else>No points found.</p>

    <button @click="logout">Logout</button>
  </div>

  <div id="notification" class="notification" style="display: none;"></div>
</template>

<script>
import { api } from '../api';

export default {
  name: 'PersonalPage',
  data() {
    return {
      user: null,
      points: [],
      point: {
        x: 1.0,
        y: 1.0,
        r: 1.0,
      },
    };
  },
  async created() {
    await this.fetchPersonalPage();
    await this.fetchPoints();
    // this.drawGraph();
  },
  methods: {
    async fetchPersonalPage() {
      try {
        const response = await api.getPersonalPage();
        this.user = response.data;
      } catch (error) {
        console.error('Error fetching personal page:', error);
      }
    },
    async fetchPoints() {
      try {
        const authToken = localStorage.getItem('authToken');
        const response = await api.getPoints(authToken);
        if (Array.isArray(response.data) && response.data.length > 0) {
          this.points = response.data;
          this.drawGraph();
          this.points.forEach((point) => {if (this.point.r === point.r) {this.drawPoint(point);}})
        }
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.showNotification("Your session expired. You will be logged out in several seconds");
          setTimeout(function () {
            localStorage.removeItem('authToken');
            window.location.href = '/';
          }, 5000)
        }
        console.error('Error fetching points:', error);
      }
    },
    draw() {
      this.drawGraph();
      if (this.points.length > 0) {
        this.points.forEach((point) => {if (this.point.r === point.r) {this.drawPoint(point);}})
      }
    },
    handleCanvasClick(event) {
      const canvas = document.getElementById('graphCanvas');
      const rect = canvas.getBoundingClientRect();
      const x = (event.clientX - rect.left - 400) / 50;
      const y = (400 - (event.clientY - rect.top)) / 50;
      const r = this.point.r;

      const pointData = {x, y, r};

      this.submitPoint(pointData);
    },
    async submitPoint(pointData) {
      try {
        const authToken = localStorage.getItem('authToken');
        const response = await api.submitPoint(authToken, pointData);
        if (Array.isArray(this.points)) {
          this.points.push(response.data);
          this.drawPoint(response.data);
        }
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.showNotification("Your session expired. You will be logged out in several seconds");
          setTimeout(function () {
            localStorage.removeItem('authToken');
            window.location.href = '/';
          }, 5000)
        }
        console.error('Error submitting point:', error);
      }
    },
    async submitFormPoint() {
      if (this.point.x < -5 || this.point.y > 5 || this.point.y < -5 || this.point.x > 5 ||
          this.point.r < 1 || this.point.r > 5) {
        console.log("Not correct coordinates");
      } else {
        const x = this.point.x;
        const y = this.point.y;
        const r = this.point.r;
        const pointData = {x, y, r};

        await this.submitPoint(pointData);
      }
    },
    drawGraph() {
      const canvas = document.getElementById('graphCanvas');
      const context = canvas.getContext('2d');

      context.clearRect(0, 0, canvas.width, canvas.height);

      context.strokeStyle = '#000';
      context.beginPath();
      context.moveTo(canvas.width / 2, 0);
      context.lineTo(canvas.width / 2, canvas.height);
      context.stroke();
      context.beginPath();
      context.moveTo(0, canvas.height / 2);
      context.lineTo(canvas.width, canvas.height / 2);
      context.stroke();

      for (let i = 0; i <= canvas.width; i += 50) {
        context.strokeStyle = '#ddd';
        context.beginPath();
        context.moveTo(i, 0);
        context.lineTo(i, canvas.height);
        context.stroke();

        context.beginPath();
        context.moveTo(0, i);
        context.lineTo(canvas.width, i);
        context.stroke();
      }

      const scale = 50;
      const r = this.point.r;

      context.fillStyle = this.createGradient(context, canvas, 'red');
      context.fillRect(canvas.width / 2 - scale * r, canvas.height / 2 - scale * r / 2, scale * r, scale * r / 2);

      context.beginPath();
      context.fillStyle = this.createGradient(context, canvas, 'blue');
      context.moveTo(canvas.width / 2, canvas.height / 2);
      context.arc(canvas.width / 2, canvas.height / 2, scale * r, -1.5 * Math.PI, -1 * Math.PI, false);
      context.lineTo(canvas.width / 2, canvas.height / 2);
      context.fill();

      context.beginPath();
      context.fillStyle = this.createGradient(context, canvas, 'rgb(249, 187, 55)');
      context.moveTo(canvas.width / 2, canvas.height / 2);
      context.lineTo(canvas.width / 2 + scale * r, canvas.height / 2);
      context.lineTo(canvas.width / 2, canvas.height / 2 + scale * r);
      context.closePath();
      context.fill();
    },
    createGradient(context, canvas, color) {
      const gradient = context.createLinearGradient(0, 0, canvas.width, canvas.height);
      gradient.addColorStop(0, color);
      gradient.addColorStop(1, "white");
      return gradient;
    },
    drawPoint(point) {
      const canvas = document.getElementById('graphCanvas');
      const context = canvas.getContext('2d');
      const x = canvas.width / 2 + point.x * 50;
      const y = canvas.height / 2 - point.y * 50;

      context.fillStyle = point.is_inside ? 'green' : 'red';
      context.beginPath();
      context.arc(x, y, 5, 0, 2 * Math.PI);
      context.fill();
    },
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
    logout() {
      localStorage.removeItem('authToken');
      this.$emit('user-logged-out');
    },
  },
};
</script>

<style src="../assets/style/screenSize.css"></style>
<style scoped>
.personal-page {
  max-width: 800px;
  margin: 0 auto;
}

canvas {
  border: 1px solid #ddd;
  margin: 20px auto;
  display: block;
}

.notification {
  position: fixed;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(255, 192, 203, 0.85);
  color: white;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  transition: opacity 0.5s ease;
  opacity: 0;
  z-index: 1000;
  display: none;
  align-items: center;
}
</style>
