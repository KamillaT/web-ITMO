<template>
  <div class="container personal-page">
    <h2>Your Personal Page</h2>
    <div v-if="user">
      <p><strong>Email:</strong> {{ user.email }}</p>
      <p><strong>Username:</strong> {{ user.username }}</p>
    </div>

    <div class="form-graph-container">
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
        <button class="btn" type="submit">Check!</button>
      </div>
    </form>

    <div class="twoTablesContainer">
    <div class="twoTables">
    <h3>Graph</h3>
    <canvas id="graphCanvas" class="graph" width="500" height="500" @click="handleCanvasClick"></canvas>
    </div>
    </div>

    <h3>Your Points</h3>
    <div id="overflow" class="twoTables" style="width: 70%;">
    <table v-if="points && points.length" style="width: 100%">
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
          <td>{{ point.inside ? 'Yes' : 'No' }}</td>
          <td>{{ point.executionTime }} ms</td>
          <td>{{ point.createdAt ? new Date(point.createdAt).toLocaleString() : 'Invalid Date' }}</td>
        </tr>
      </tbody>
    </table>
    <p v-else>No points found.</p>
    </div>
  </div>

    <button class="btn" @click="logout">Logout</button>
  </div>

  <div id="notification" class="notification" style="display: none;"></div>
</template>

<script>
import { api } from '../api';
import { handleApiError } from '../utils';

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
        this.user = response;
      } catch (error) {
        // console.error('Error fetching personal page:', error);
        handleApiError(error, this.showNotification);
      }
    },
    async fetchPoints() {
      try {
        const authToken = localStorage.getItem('authToken');
        const response = await api.getPoints(authToken);
        if (Array.isArray(response) && response.length > 0) {
          this.points = response;
          this.drawGraph();
          this.points.forEach((point) => {if (this.point.r === point.r) {this.drawPoint(point);}})
        } else {
          this.drawGraph();
        }
      } catch (error) {
        handleApiError(error, this.showNotification);
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
      const q1 = canvas.width / 2;
      const q2 = canvas.height / 2;
      const x = (event.clientX - rect.left - q1) / 50;
      const y = (q2 - (event.clientY - rect.top)) / 50;
      const r = this.point.r;

      const pointData = {x, y, r};

      this.submitPoint(pointData);
    },
    async submitPoint(pointData) {
      try {
        const response = await api.submitPoint(pointData);
        if (Array.isArray(this.points)) {
          this.points.push(response);
          this.drawPoint(response);
        }
      } catch (error) {
        handleApiError(error, this.showNotification);
      }
    },
    async submitFormPoint() {
      if (this.point.x < -5 || this.point.y > 5 || this.point.y < -5 || this.point.x > 5 ||
          this.point.r < 1 || this.point.r > 5) {
        this.showNotification("Not correct coordinates");
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

      context.fillStyle = point.inside ? 'green' : 'red';
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
  background-color: rgba(144, 238, 144, 0.85); /* 将背景颜色修改为浅绿色 */
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
